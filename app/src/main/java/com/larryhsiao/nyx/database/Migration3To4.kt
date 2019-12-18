package com.larryhsiao.nyx.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration version 2 to 3.
 */
class Migration3To4 : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE diary ADD COLUMN weather_id INTEGER;")
        // language=SQLite
        database.execSQL("CREATE TABLE weather (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "iconUrl TEXT," +
                "raw TEXT NOT NULL" +
                ");")
    }
}