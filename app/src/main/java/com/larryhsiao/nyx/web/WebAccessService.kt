package com.larryhsiao.nyx.web

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.larryhsiao.nyx.database.RDatabase


/**
 * Service for web access server
 */
class WebAccessService : Service() {
    private lateinit var webAccess: WebAccess
    override fun onCreate() {
        super.onCreate()
        webAccess = TakesAccess(
            this,
            RDatabase.Singleton(this).value()
        )
        webAccess.enable()
    }

    override fun onDestroy() {
        super.onDestroy()
        webAccess.disable()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
