package com.larryhsiao.nyx

import android.os.Bundle
import com.larryhsiao.nyx.view.backup.BackupListFragment
import com.larryhsiao.nyx.view.diary.CalendarFragment
import com.larryhsiao.nyx.view.diary.EventListFragment
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

        main_bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_calendar -> {
                    rootPage(CalendarFragment())
                }
                R.id.navigation_jotted->{
                    rootPage(EventListFragment())
                }
                R.id.navigation_restore -> {
                    rootPage(BackupListFragment())
                }
            }

            true
        }
        main_bottomNavigation.selectedItemId = R.id.navigation_jotted
    }
}

