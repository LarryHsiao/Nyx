package com.larryhsiao.nyx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity for debug purpose.
 */
class DebugActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.debug_panel)
    }
}