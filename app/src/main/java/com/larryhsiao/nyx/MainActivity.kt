package com.larryhsiao.nyx

import android.os.Bundle
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.view.backup.BackupListFragment
import com.larryhsiao.nyx.view.diary.CalendarFragment
import com.larryhsiao.nyx.view.diary.EventListFragment
import com.larryhsiao.nyx.view.settings.BioAuth
import com.larryhsiao.nyx.view.settings.SettingFragment
import com.larryhsiao.nyx.web.TakesAccess
import com.silverhetch.aura.AuraActivity
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Main entrance of this activity.
 */
class MainActivity : AuraActivity() {
    companion object {
        private const val SAVED_STATE_BIO_AUTH = "SAVED_STATE_BIO_AUTH"
    }

    private var bioAuth = false
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
                R.id.navigation_jotted -> {
                    rootPage(EventListFragment())
                }
                R.id.navigation_restore -> {
                    rootPage(BackupListFragment())
                }
                R.id.navigation_setting -> {
                    rootPage(SettingFragment())
                }
            }
            true
        }
        if (savedInstanceState==null) {
            main_bottomNavigation.selectedItemId = R.id.navigation_jotted
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_STATE_BIO_AUTH, bioAuth)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        bioAuth = savedInstanceState?.getBoolean(SAVED_STATE_BIO_AUTH) ?: false
    }

    override fun onResume() {
        super.onResume()
        TakesAccess(RDatabase.Singleton(this).value()).enable()
        if (bioAuth.not() && ConfigImpl(this).bioAuthEnabled()) {
            BioAuth(this, { bioAuth = true }) { _, err ->
                finishAffinity()
            }.fire()
        }
    }
}

