package com.larryhsiao.nyx.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration version 2 to 3.
 */
class Migration2To3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""CREATE TABLE IF NOT EXISTS `tag_diary` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `diary_id` INTEGER NOT NULL, `tag_id` INTEGER NOT NULL);""")
        database.execSQL("""CREATE UNIQUE INDEX `index_tag_diary_diary_id_tag_id` ON `tag_diary` (`diary_id`, `tag_id`);""")
    }
}