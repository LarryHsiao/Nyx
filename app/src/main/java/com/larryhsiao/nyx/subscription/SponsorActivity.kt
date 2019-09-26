package com.larryhsiao.nyx.subscription

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.OK
import com.android.billingclient.api.BillingClient.SkuType.SUBS
import com.larryhsiao.nyx.R
import com.silverhetch.aura.AuraActivity
import kotlinx.android.synthetic.main.item_support_coffee.*

/**
 * Activity for sponsor billing.
 */
class SponsorActivity : AuraActivity(), PurchasesUpdatedListener {
    companion object {
        private const val PRODUCT_ID_SPONSOR = "sponsor"
    }

    private val client by lazy {
        BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_sponsor)
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                client.startConnection(this)
            }

            override fun onBillingSetupFinished(billingResult: BillingResult?) {
                if (billingResult?.responseCode != OK) {
                    return
                }

                client.querySkuDetailsAsync(
                    SkuDetailsParams.newBuilder()
                        .setSkusList(listOf(PRODUCT_ID_SPONSOR))
                        .setType(SUBS)
                        .build()
                ) { result, skus ->
                    if (result.responseCode == OK && skus != null) {
                        for (sku in skus) {
                            if (PRODUCT_ID_SPONSOR == sku.sku) {
                                updateSponsor(sku)
                                updateSubscriptionState()
                            }
                        }
                    }
                }
            }
        })
    }

    private fun updateSponsor(sku: SkuDetails) {
        itemSupportCoffee_supportButton.text = sku.price ?: ""
        itemSupportCoffee_supportButton.setOnClickListener {
            client.launchBillingFlow(
                this,
                BillingFlowParams.newBuilder()
                    .setSkuDetails(sku)
                    .build()
            )
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult?,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult?.responseCode == OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else {
            error()
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        when (purchase.sku) {
            PRODUCT_ID_SPONSOR -> {
                updateSubscriptionState()
            }
            else -> {
                error()
            }
        }
    }

    private fun error() {
        Toast.makeText(
            this,
            R.string.appError_unknown,
            LENGTH_SHORT
        ).show()
    }

    private fun updateSubscriptionState() {
        client.queryPurchases(SUBS).purchasesList.forEach {
            if (it.sku == PRODUCT_ID_SPONSOR) {
                itemSupportCoffee_supportButton.isClickable = false
                itemSupportCoffee_supportButton.setText(R.string.thanks)
            }
        }
    }
}