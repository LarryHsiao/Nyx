package com.larryhsiao.nyx.backup.local

import com.larryhsiao.nyx.backup.Backup
import java.io.File

/**
 * Represent a backup instance at local storage.
 */
class FileBackup(private val backupRoot: File) : Backup {
    override fun title(): String {
        return backupRoot.name
    }
}