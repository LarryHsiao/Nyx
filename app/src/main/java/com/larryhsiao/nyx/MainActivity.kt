package com.larryhsiao.nyx

import android.os.Bundle
import com.larryhsiao.nyx.view.diary.CalendarFragment
import com.silverhetch.aura.AuraActivity
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Main entrance of this activity.
 */
class MainActivity : AuraActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupFabControl(main_fab)
        setupPageControl(R.id.main_fragmentContainer)

        rootPage(CalendarFragment())
    }
}

