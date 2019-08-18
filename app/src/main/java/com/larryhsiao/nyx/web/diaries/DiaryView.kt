package com.larryhsiao.nyx.web.diaries

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.larryhsiao.nyx.diary.room.RDiary
import com.silverhetch.clotho.Source

/**
 * Adapter to convert the raw entity into client data view.
 */
class DiaryView(private val diaries: List<RDiary>) : Source<String> {
    override fun value(): String {
        return JsonArray().apply {
            diaries.forEach {
                add(JsonObject().apply {
                    addProperty("id", it.diary.id)
                    addProperty("title", it.diary.title)
                    add("attachments", JsonArray().apply {
                        it.mediaEntities.forEach {
                            add(it.uri.split("/").last())
                        }
                    })
                })
            }
        }.toString()
    }
}