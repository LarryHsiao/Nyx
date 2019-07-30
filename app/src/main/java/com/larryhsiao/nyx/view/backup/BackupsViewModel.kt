package com.larryhsiao.nyx.view.backup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.larryhsiao.nyx.ConfigImpl
import com.larryhsiao.nyx.backup.Backup
import com.larryhsiao.nyx.backup.local.Allbackups
import com.larryhsiao.nyx.backup.local.BackupRootSource
import com.larryhsiao.nyx.backup.local.NewBackup
import com.larryhsiao.nyx.database.RDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * View Model of backups, represent the backup instances.
 */
class BackupsViewModel(app: Application) : AndroidViewModel(app) {
    private val db = RDatabase.Factory(app).value()
    private val config = ConfigImpl(app)
    private val backups =
        MutableLiveData<List<Backup>>().apply { value = listOf() }

    /**
     * Live data of backup list
     */
    fun backups(): LiveData<List<Backup>> {
        return backups
    }

    /**
     * Do the fetching backup list.
     */
    fun fetch() {
        GlobalScope.launch {
            try {
                backups.postValue(
                    Allbackups(config).value()
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Trigger a new backup process
     */
    fun newBackup() {
        GlobalScope.launch {
            NewBackup(
                db,
                BackupRootSource().value()
            ).fire()
            fetch()
        }
    }


}