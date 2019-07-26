package com.larryhsiao.nyx.backup

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.silverhetch.clotho.Action
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

/**
 * Import diary from json file.
 */
class DiaryImport(
    private val db: RDatabase,
    private val jsonFile: File
) : Action {
    override fun fire() {
        val gson = Gson()
        JsonReader(
            InputStreamReader(
                FileInputStream(jsonFile)
            )
        ).use { reader ->
            reader.beginArray()
            while (reader.hasNext()) {
                db.diaryDao().create(
                    gson.fromJson(reader, DiaryEntity::class.java)
                )
            }
            reader.endArray()
        }
    }
}