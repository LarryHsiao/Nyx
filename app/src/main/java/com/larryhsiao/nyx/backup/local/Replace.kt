package com.larryhsiao.nyx.backup.local

import com.larryhsiao.nyx.Config
import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.Action
import com.silverhetch.clotho.file.FileDelete
import java.io.File

/**
 * Replace entire database/internal files with backup one.
 */
class Replace(
    private val backupRoot: File,
    private val config: Config,
    private val db: RDatabase
) : Action {
    override fun fire() {
        db.mediaDao().clear()
        db.diaryDao().clear()
        FileDelete(config.mediaRoot()).fire()
        Restore(
            backupRoot,
            config,
            db
        ).fire()
    }
}