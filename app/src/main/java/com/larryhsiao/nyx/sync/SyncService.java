package com.larryhsiao.nyx.sync;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import com.android.billingclient.api.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.larryhsiao.nyx.JotApplication;
import com.larryhsiao.nyx.ServiceIds;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.util.List;

import static com.android.billingclient.api.BillingClient.SkuType.SUBS;
import static com.android.billingclient.api.Purchase.PurchaseState.PURCHASED;

/**
 * Service to sync data to server.
 *
 * @todo #1 Inform user to resolve conflict if the local data will be override.
 */
public class SyncService extends JobIntentService
    implements ServiceIds,
    PurchasesUpdatedListener,
    BillingClientStateListener {
    private Source<Connection> db;
    private BillingClient client;

    public static void enqueue(Context context) {
        enqueueWork(context, SyncService.class, SYNC, new Intent());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        db = ((JotApplication) getApplication()).db;
        new LocalFileSync(this, db, integer -> null).fire();

        client = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build();
        client.startConnection(this);
        syncNonPremium();
    }

    private void syncNonPremium() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        new SyncJots(user.getUid(), db).fire();
        new SyncTags(user.getUid(), db).fire();
        new SyncTagJot(user.getUid(), db).fire();
    }

    private void syncPremium() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        new SyncAttachments(this, user.getUid(), db).fire();
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
        List<Purchase> purchasesList =
            client.queryPurchases(SUBS).getPurchasesList();
        for (Purchase purchase : purchasesList) {
            if ("premium".equals(purchase.getSku()) &&
                purchase.getPurchaseState() == PURCHASED) {
                syncPremium();
                return;
            }
        }
    }

    @Override
    public void onBillingServiceDisconnected() {

    }
}
