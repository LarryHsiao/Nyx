package com.larryhsiao.nyx.view.backup

import android.os.Bundle
import com.larryhsiao.nyx.R
import com.silverhetch.aura.AuraActivity
import kotlinx.android.synthetic.main.layout_fab_fragment.*

/**
 * Activity for operating restore/backup frunction
 */
class RestoreActivity : AuraActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_fab_fragment)
        setupFabControl(fabFragment_fab)

        supportFragmentManager.beginTransaction()
            .replace(fabFragment_container.id, BackupListFragment())
            .commit()
    }
}