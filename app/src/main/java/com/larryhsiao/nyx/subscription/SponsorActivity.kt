package com.larryhsiao.nyx.subscription

import android.os.Bundle
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.OK
import com.android.billingclient.api.BillingClient.SkuType.INAPP
import com.silverhetch.aura.AuraActivity
import com.larryhsiao.nyx.R

/**
 * Activity for sponsor billing.
 */
class SponsorActivity : AuraActivity(), PurchasesUpdatedListener {
    companion object {
        private const val PRODUCT_ID_SPONSOR = "sponsor"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_sponsor)
        val client = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this).build()
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                client.startConnection(this)
            }

            override fun onBillingSetupFinished(billingResult: BillingResult?) {
                client.querySkuDetailsAsync(
                    SkuDetailsParams.newBuilder()
                        .setSkusList(listOf(PRODUCT_ID_SPONSOR))
                        .setType(INAPP)
                        .build()
                ) { result, skus ->
                    if (result.responseCode == OK && skus != null) {
                        for (sku in skus) {
                            if (PRODUCT_ID_SPONSOR == sku.sku) {
                                updateSponsor(sku)
                            }
                        }
                    }
                }

            }
        })
    }

    private fun updateSponsor(sku: SkuDetails?) {

    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult?,
        purchases: MutableList<Purchase>?
    ) {
    }
}