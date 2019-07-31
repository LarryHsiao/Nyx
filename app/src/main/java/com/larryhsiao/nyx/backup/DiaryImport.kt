package com.larryhsiao.nyx.backup

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.silverhetch.clotho.Action
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Import diary from json file.
 */
class DiaryImport(
    private val db: RDatabase,
    private val jsonStream: InputStream
) : Action {
    override fun fire() {
        val gson = Gson()
        JsonReader(InputStreamReader(jsonStream)).use { reader ->
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