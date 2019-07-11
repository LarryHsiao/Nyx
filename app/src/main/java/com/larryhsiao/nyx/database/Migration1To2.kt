package com.larryhsiao.nyx.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration version 1 to 2.
 */
class Migration1To2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS media;")
        database.execSQL(
            """CREATE TABLE IF NOT EXISTS `media` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
            `diary_id` INTEGER NOT NULL, 
            `uri` TEXT NOT NULL);""")
        database.execSQL("DROP TABLE IF EXISTS tag;")
        database.execSQL(
            """CREATE TABLE IF NOT EXISTS `tag` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `title` TEXT NOT NULL); """)
        database.execSQL("""CREATE UNIQUE INDEX `index_tag_title` ON `tag` (`title`);""")
    }
}