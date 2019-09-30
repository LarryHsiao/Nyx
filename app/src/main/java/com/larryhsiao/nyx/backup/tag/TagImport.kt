package com.larryhsiao.nyx.backup.tag

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.tag.room.TagEntity
import com.silverhetch.clotho.Action
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Import tag from json file.
 */
class TagImport(
    private val db: RDatabase,
    private val jsonStream: InputStream
) : Action {
    override fun fire() {
        val gson = Gson()
        JsonReader(InputStreamReader(jsonStream)).use { reader ->
            reader.beginArray()
            while (reader.hasNext()) {
                db.tagDao().create(
                    gson.fromJson(reader, TagEntity::class.java)
                )
            }
            reader.endArray()
        }
    }
}