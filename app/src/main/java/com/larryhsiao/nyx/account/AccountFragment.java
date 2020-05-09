package com.larryhsiao.nyx.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.backup.google.DriveBackupFragment;
import com.larryhsiao.nyx.base.JotFragment;
import com.larryhsiao.nyx.sync.PremiumFragment;
import com.silverhetch.aura.view.alert.Alert;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.android.billingclient.api.BillingClient.BillingResponseCode.OK;
import static com.android.billingclient.api.BillingClient.SkuType.INAPP;
import static com.android.billingclient.api.BillingClient.SkuType.SUBS;
import static com.android.billingclient.api.Purchase.PurchaseState.PURCHASED;
import static com.android.billingclient.api.SkuDetailsParams.newBuilder;
import static com.larryhsiao.nyx.JotApplication.SKU_DRIVE_BACKUP;
import static com.larryhsiao.nyx.JotApplication.SKU_PREMIUM;

/**
 * Account page
 */
public class AccountFragment extends JotFragment implements PurchasesUpdatedListener {
    private static final int REQUEST_CODE_ERROR = 1000;
    private BillingClient billing;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        billing = BillingClient.newBuilder(getContext())
            .enablePendingPurchases()
            .setListener(this)
            .build();
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.page_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        billing.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == OK) {
                    queryAvailable();
                } else {
                    Alert.Companion.newInstance(
                        REQUEST_CODE_ERROR, getString(R.string.not_available)
                    ).show(getChildFragmentManager(), null);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
            }
        });
    }

    private void queryAvailable() {
        Set<String> available = new HashSet<>();
        List<Purchase> purchasesList = billing.queryPurchases(INAPP).getPurchasesList();
        if (purchasesList != null) {
            for (Purchase purchase : purchasesList) {
                if (SKU_DRIVE_BACKUP.equals(purchase.getSku())
                    && purchase.getPurchaseState() == PURCHASED) {
                    available.add(SKU_DRIVE_BACKUP);
                }
            }
        }

        List<Purchase> subList = billing.queryPurchases(SUBS).getPurchasesList();
        if (subList != null) {
            for (Purchase purchase : subList) {
                if (SKU_PREMIUM.equals(purchase.getSku())
                    && purchase.getPurchaseState() == PURCHASED) {
                    available.add(SKU_PREMIUM);
                }
            }
        }

        updateDriveBlock(available);
        updateFirebaseBlock(available);
    }

    private void updateDriveBlock(Set<String> available) {
        if (available.contains(SKU_DRIVE_BACKUP)) {
            getChildFragmentManager().beginTransaction()
                .replace(R.id.account_driveBackupContainer, new DriveBackupFragment())
                .commit();
        } else {
            View view = LayoutInflater.from(getContext()).inflate(
                R.layout.block_purchase,
                getView().findViewById(R.id.account_driveBackupContainer)
            );
            view.findViewById(R.id.blockPurchase_purchaseButton).setOnClickListener(it ->
                launchPurchase(SKU_DRIVE_BACKUP, INAPP)
            );
        }
    }

    private void updateFirebaseBlock(Set<String> available) {
        if (available.contains(SKU_PREMIUM)) {
            getChildFragmentManager().beginTransaction()
                .replace(R.id.account_firebaseSyncContainer, new PremiumFragment())
                .commit();
        } else {
            // @todo #1 Detect user unsubscribed every time launch the app.
            FirebaseAuth.getInstance().signOut();
            View view = LayoutInflater.from(getContext()).inflate(
                R.layout.block_purchase,
                getView().findViewById(R.id.account_firebaseSyncContainer)
            );
            view.findViewById(R.id.blockPurchase_purchaseButton).setOnClickListener(it ->
                launchPurchase(SKU_PREMIUM, SUBS)
            );
        }
    }

    private void launchPurchase(String skuType, String purchaseType) {
        billing.querySkuDetailsAsync(
            newBuilder()
                .setSkusList(Arrays.asList(skuType))
                .setType(purchaseType)
                .build(),
            (billingResult, list) -> {
                if (billingResult.getResponseCode() != OK) {
                    return;
                }
                for (SkuDetails skuDetails : list) {
                    if (skuType.equals(skuDetails.getSku())) {
                        billing.launchBillingFlow(
                            getActivity(),
                            BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetails)
                                .build()
                        );
                    }
                }
            }
        );
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() != OK || list == null) {
            return;
        }
        for (Purchase purchase : list) {
            if (purchase.getPurchaseState() == PURCHASED
                && SKU_DRIVE_BACKUP.equals(purchase.getSku())) {
                if (!purchase.isAcknowledged()) {
                    billing.acknowledgePurchase(
                        AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build(),
                        res -> {
                            ((ViewGroup) getView().findViewById(R.id.account_driveBackupContainer)).removeAllViews();
                            getChildFragmentManager().beginTransaction()
                                .replace(R.id.account_driveBackupContainer, new DriveBackupFragment())
                                .commit();
                        }
                    );
                }
            }
        }
    }

}
