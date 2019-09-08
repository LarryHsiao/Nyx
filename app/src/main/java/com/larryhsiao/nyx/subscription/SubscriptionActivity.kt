package com.larryhsiao.nyx.subscription

import android.os.Bundle
import com.android.billingclient.api.*
import com.silverhetch.aura.AuraActivity

/**
 * Activity for Subscription related functions.
 */
class SubscriptionActivity : AuraActivity(), PurchasesUpdatedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val client = BillingClient.newBuilder(this).setListener(this).build()
        client.startConnection(object:BillingClientStateListener{
            override fun onBillingServiceDisconnected() {
                client.startConnection(this)
            }

            override fun onBillingSetupFinished(billingResult: BillingResult?) {
            }
        })
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult?,
        purchases: MutableList<Purchase>?
    ) {
    }
}