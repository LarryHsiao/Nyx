package com.larryhsiao.nyx.sync;

import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.android.billingclient.api.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.larryhsiao.nyx.JotApplication;
import com.larryhsiao.nyx.KeyChangingActivity;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.ServiceIds;
import com.larryhsiao.nyx.account.api.ChangeEncryptKeyReq;
import com.larryhsiao.nyx.account.api.NyxApi;
import com.larryhsiao.nyx.sync.encryption.JasyptStringEncryptor;
import com.larryhsiao.nyx.sync.encryption.StringEncryptor;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.encryption.MD5;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.app.PendingIntent.getActivity;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.O;
import static androidx.core.app.NotificationCompat.Builder;
import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;
import static androidx.core.app.NotificationManagerCompat.from;
import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.android.billingclient.api.BillingClient.SkuType.SUBS;
import static com.android.billingclient.api.Purchase.PurchaseState.PURCHASED;
import static com.larryhsiao.nyx.NotificationIds.CHANNEL_ID_SYNC;
import static com.larryhsiao.nyx.NotificationIds.NOTIFICATION_ID_INVALID_ENCRYPT_KEY;

/**
 * Service to sync data to server.
 *
 * @todo #1 Inform user to resolve conflict if the local data will be override.
 */
public class SyncService extends JobIntentService
    implements ServiceIds,
    PurchasesUpdatedListener,
    BillingClientStateListener {
    private static final int REQUEST_CODE_ENCRYPTION_KEY_NOT_MATCHED = 1000;
    private Source<Connection> db;
    private BillingClient client;

    public static void enqueue(Context context) {
        enqueueWork(context, SyncService.class, SYNC, new Intent());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        db = ((JotApplication) getApplication()).db;
        new LocalFileSync(this, db, integer -> null).fire();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            cancelKeyNotMatchNotification();
            return;
        }
        final String encryptKey = encryptKey();
        final String keyHash = new MD5(new ByteArrayInputStream(encryptKey.getBytes())).value();
        final CollectionReference userRef = FirebaseFirestore.getInstance()
            .collection(user.getUid());
        userRef.document("account").get().addOnSuccessListener(doc -> {
            final DocumentReference dataRef = userRef.document(keyHash);
            if (doc.contains("key_hash")) {
                if ((keyHash + "").equals(doc.getString("key_hash"))) {
                    cancelKeyNotMatchNotification();
                    syncToCloud(dataRef, new JasyptStringEncryptor(encryptKey));
                } else {
                    notifyKeyNotMatch();
                }
            } else {
                // Ignore failure, try again next time sync
                final ChangeEncryptKeyReq req = new ChangeEncryptKeyReq();
                req.keyHash = keyHash;
                req.uid = user.getUid();
                NyxApi.client().changeEncryptKey(req).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            cancelKeyNotMatchNotification();
                            syncToCloud(dataRef, new JasyptStringEncryptor(encryptKey));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                    }
                });
            }
        });
    }

    private void cancelKeyNotMatchNotification(){
        from(this).cancel(NOTIFICATION_ID_INVALID_ENCRYPT_KEY);
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
        DocumentReference dataRef,
        JasyptStringEncryptor encrypt
    ) {
        client = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build();
        client.startConnection(this);
        syncNonPremium(dataRef, encrypt);
    }

    private String encryptKey() {
        SharedPreferences pref = getDefaultSharedPreferences(this);
        String encryptKey = pref.getString("encrypt_key", "");
        if (encryptKey.isEmpty()) {
            pref.edit().putString(
                "encrypt_key",
                encryptKey = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 7)
            ).apply();
        }
        return encryptKey;
    }

    private void syncNonPremium(
        DocumentReference dataRef,
        StringEncryptor encrypt
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

    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        List<Purchase> purchasesList = client.queryPurchases(SUBS)
            .getPurchasesList();
        for (Purchase purchase : purchasesList) {
            if ("premium".equals(purchase.getSku()) &&
                purchase.getPurchaseState() == PURCHASED) {
                new SyncAttachments(this, user.getUid(), db).fire();
                return;
            }
        }
    }

    @Override
    public void onBillingServiceDisconnected() {

    }
}
