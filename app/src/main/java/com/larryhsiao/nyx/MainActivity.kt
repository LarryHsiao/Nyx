package com.larryhsiao.nyx

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.larryhsiao.nyx.core.server.NyxServer
import com.larryhsiao.nyx.server.ServerService


/**
 * Entry Activity of Nyx.
 */
class MainActivity : AppCompatActivity(), ServiceConnection {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        bindService(
            Intent(
                this,
                ServerService::class.java
            ),
            this,
            Service.BIND_AUTO_CREATE
        )
    }

    override fun onPause() {
        super.onPause()
        unbindService(this)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
    }

    override fun onServiceDisconnected(name: ComponentName?) {
    }
}