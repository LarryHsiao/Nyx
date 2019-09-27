package com.larryhsiao.nyx.backup.local

import com.larryhsiao.nyx.config.Config
import com.larryhsiao.nyx.backup.Backup
import com.silverhetch.clotho.Source

/**
 * Source to build all backup instance at local storage
 */
class Allbackups(private val config: Config) : Source<List<Backup>> {
    override fun value(): List<Backup> {
        val files = config.backupRoot().listFiles()
        return Array(files.size) {
            FileBackup(files[it])
        }.toList()
    }
}