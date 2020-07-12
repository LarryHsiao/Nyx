package com.larryhsiao.nyx.sync;

import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.android.billingclient.api.*;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.larryhsiao.nyx.JotApplication;
import com.larryhsiao.nyx.KeyChangingActivity;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.ServiceIds;
import com.larryhsiao.nyx.account.action.UpdateLastSyncedAction;
import com.larryhsiao.nyx.account.api.ChangeEncryptKeyReq;
import com.larryhsiao.nyx.account.api.NyxApi;
import com.larryhsiao.nyx.settings.DefaultPreference;
import com.larryhsiao.nyx.settings.NyxSettings;
import com.larryhsiao.nyx.settings.NyxSettingsImpl;
import com.larryhsiao.nyx.sync.encryption.Encryptor;
import com.larryhsiao.nyx.sync.encryption.JasyptStringEncryptor;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.encryption.MD5;
import com.silverhetch.clotho.source.SingleRefSource;
import retrofit2.Response;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;
import static android.app.NotificationManager.IMPORTANCE_LOW;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.app.PendingIntent.getActivity;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.O;
import static androidx.core.app.NotificationCompat.*;
import static androidx.core.app.NotificationManagerCompat.from;
import static com.android.billingclient.api.BillingClient.SkuType.SUBS;
import static com.android.billingclient.api.Purchase.PurchaseState.PURCHASED;
import static com.larryhsiao.nyx.NotificationIds.*;
import static com.larryhsiao.nyx.NyxActions.SYNC_CHECKPOINT;

/**
 * Service to sync data to server.
 *
 * @todo #1 Inform user to resolve conflict if the local data will be override.
 */
public class SyncService extends JobIntentService
    implements ServiceIds, PurchasesUpdatedListener {
    private static final int REQUEST_CODE_ENCRYPTION_KEY_NOT_MATCHED = 1000;
    private Source<Connection> db;
    private NyxSettings settings;

    public static void enqueue(Context context) {
        enqueueWork(context, SyncService.class, SYNC, new Intent());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        try {
            from(this).cancel(NOTIFICATION_ID_SYNCING);
            settings = new NyxSettingsImpl(new SingleRefSource<>(new DefaultPreference(this)));
            db = ((JotApplication) getApplication()).db;
            new LocalFileSync(this, db).fire();
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                cancelKeyNotMatchNotification();
                from(this).cancel(NOTIFICATION_ID_SYNCING);
                return;
            }
            syncAuthCheck(user);
            new UpdateLastSyncedAction(this).fire();
            from(this).cancel(NOTIFICATION_ID_SYNCING);
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SYNC_CHECKPOINT));
        } catch (Exception e) {
            e.printStackTrace();
            from(this).cancel(NOTIFICATION_ID_SYNCING);
        }
    }

    private void syncAuthCheck(FirebaseUser user) throws Exception {
        final String encryptKey = settings.encryptionKey();
        final String keyHash = new MD5(new ByteArrayInputStream(encryptKey.getBytes())).value();
        final CollectionReference userRef =
            FirebaseFirestore.getInstance().collection(user.getUid());
        Task<DocumentSnapshot> task = userRef.document("account").get();
        DocumentSnapshot doc = Tasks.await(task);
        final DocumentReference dataRef = userRef.document(keyHash);
        if (doc.contains("key_hash")) {
            if ((keyHash + "").equals(doc.getString("key_hash"))) {
                cancelKeyNotMatchNotification();
                syncToCloud(user, dataRef, new JasyptStringEncryptor(encryptKey));
            } else {
                notifyKeyNotMatch();
            }
        } else {
            // Ignore failure, try again next time sync
            final ChangeEncryptKeyReq req = new ChangeEncryptKeyReq();
            req.keyHash = keyHash;
            Response<Void> response = NyxApi.client().changeEncryptKey(
                "Bearer " + Tasks.await(user.getIdToken(true)).getToken(),
                req
            ).execute();
            if (response.isSuccessful()) {
                cancelKeyNotMatchNotification();
                syncToCloud(user, dataRef, new JasyptStringEncryptor(encryptKey));
            }
        }
    }

    private void cancelKeyNotMatchNotification() {
        from(this).cancel(NOTIFICATION_ID_INVALID_ENCRYPT_KEY);
    }

    private void notifySyncing(int total, int progress) {
        NotificationManagerCompat mgr = from(this);
        if (SDK_INT >= O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID_SYNCING,
                getString(R.string.Sync_service),
                IMPORTANCE_LOW
            );
            channel.setShowBadge(false);
            channel.setDescription(getString(R.string.Service_syncs_jots_to_cloud));
            mgr.createNotificationChannel(channel);
        }
        mgr.notify(
            NOTIFICATION_ID_SYNCING,
            new Builder(this, CHANNEL_ID_SYNCING)
                .setSmallIcon(R.drawable.ic_jotted)
                .setPriority(PRIORITY_LOW)
                .setAutoCancel(false)
                .setOngoing(true)
                .setProgress(total, progress, false)
                .setContentTitle(getString(R.string.Jotted_is_syncing))
                .setContentText(getString(R.string.Syncing______, progress+"", total+""))
                .build()
        );
    }

    private void notifyKeyNotMatch() {
        NotificationManagerCompat mgr = from(this);
        if (SDK_INT >= O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID_SYNC,
                getString(R.string.Sync_service),
                IMPORTANCE_DEFAULT
            );
            channel.setDescription(getString(R.string.Service_syncs_jots_to_cloud));
            mgr.createNotificationChannel(channel);
        }
        mgr.notify(
            NOTIFICATION_ID_INVALID_ENCRYPT_KEY,
            new Builder(this, CHANNEL_ID_SYNC)
                .setSmallIcon(R.drawable.ic_jotted)
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(getString(
                        R.string.The_local_encryption_key_is_not_match_to_cloud_one_please_choose_which_one_to_keep)
                    ))
                .setPriority(PRIORITY_DEFAULT)
                .setContentTitle(getString(R.string.Encryption_key_not_valid))
                .setContentText(getString(
                    R.string.The_local_encryption_key_is_not_match_to_cloud_one_please_choose_which_one_to_keep
                ))
                .setContentIntent(getActivity(
                    this,
                    REQUEST_CODE_ENCRYPTION_KEY_NOT_MATCHED,
                    new Intent(this, KeyChangingActivity.class),
                    FLAG_UPDATE_CURRENT
                )).build()
        );
    }

    private void syncToCloud(
        FirebaseUser user,
        DocumentReference dataRef,
        JasyptStringEncryptor encrypt
    ) {
        try {
            AtomicBoolean purchased = new AtomicBoolean(false);
            BillingClient billing = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this)
                .build();
            billing.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        return;
                    }
                    List<Purchase> purchasesList = billing.queryPurchases(SUBS).getPurchasesList();
                    for (Purchase purchase : purchasesList) {
                        if ("premium".equals(purchase.getSku()) &&
                            purchase.getPurchaseState() == PURCHASED) {
                            purchased.set(true);
                            return;
                        }
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                }
            });
            Thread.sleep(1000); // Wait for the purchase status
            billing.endConnection();
            syncNonPremium(dataRef, encrypt);
            if (purchased.get()) {
                new SyncAttachments(
                    this,
                    dataRef,
                    db,
                    user.getUid(),
                    settings.encryptionKey()
                ).fire();
                new LocalFileSync(this, db).fire(); // for deleted items
                new RemoteFileSync(
                    this,
                    db,
                    user.getUid(),
                    settings.encryptionKey(),
                    this::notifySyncing
                ).fire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void syncNonPremium(
        DocumentReference dataRef,
        Encryptor<String> encrypt
    ) {
        new SyncJots(dataRef, db, encrypt).fire();
        new SyncTags(dataRef, db, encrypt).fire();
        new SyncTagJot(dataRef, db).fire();
    }

    @Override
    public void onPurchasesUpdated(
        BillingResult res,
        @Nullable List<Purchase> list
    ) {
        // Leave it empty
    }
}
