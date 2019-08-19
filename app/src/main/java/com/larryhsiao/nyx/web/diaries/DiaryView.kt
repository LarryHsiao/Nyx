package com.larryhsiao.nyx.web.diaries

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.larryhsiao.nyx.diary.room.RDiary
import com.silverhetch.clotho.Source

/**
 * Diary view for web access client
 */
class DiaryView(private val it: RDiary) : Source<JsonObject> {
    override fun value(): JsonObject {
        return JsonObject().apply {
            addProperty("id", it.diary.id)
            addProperty("title", it.diary.title)
            add("attachments", JsonArray().apply {
                it.mediaEntities.forEach {
                    add(it.uri.split("/").last())
                }
            })
        }
    }
}