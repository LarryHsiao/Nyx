package com.larryhsiao.nyx.old.account;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetailsParams;
import com.larryhsiao.clotho.Source;

import java.util.ArrayList;
import java.util.List;

import static com.android.billingclient.api.BillingClient.SkuType.SUBS;
import static com.android.billingclient.api.Purchase.PurchaseState.PURCHASED;

/**
 * Source to fetch purchase token at play store.
 *
 * Note: The token will be validated by nyx backend when user log in.
 */
public class PlaySubToken implements Source<String> {
    private final BillingClient client;

    public PlaySubToken(BillingClient client) {
        this.client = client;
    }

    @Override
    public String value() {
        List<String> skuList = new ArrayList<>();
        skuList.add("premium");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(SUBS);
        List<Purchase> purchases = client.queryPurchases(SUBS).getPurchasesList();
        if (purchases != null) {
            for (Purchase purchase : purchases) {
                if (purchase.getPurchaseState() == PURCHASED &&
                    "premium".equals(purchase.getSku())
                ) {
                    return purchase.getPurchaseToken();
                }
            }
        }
        return "";
    }
}
