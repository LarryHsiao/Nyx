package com.larryhsiao.nyx.backup.tag

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.tag.room.TagDiaryEntity
import com.silverhetch.clotho.Action
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Import tag from json file.
 */
class TagDiaryImport(
    private val db: RDatabase,
    private val jsonStream: InputStream
) : Action {
    override fun fire() {
        val gson = Gson()
        JsonReader(InputStreamReader(jsonStream)).use { reader ->
            reader.beginArray()
            while (reader.hasNext()) {
                db.tagDiaryDao().create(
                    gson.fromJson(reader, TagDiaryEntity::class.java)
                )
            }
            reader.endArray()
        }
    }
}