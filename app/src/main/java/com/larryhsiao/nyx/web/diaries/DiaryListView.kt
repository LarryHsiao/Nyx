package com.larryhsiao.nyx.web.diaries

import com.google.gson.JsonArray
import com.larryhsiao.nyx.diary.room.RDiary
import com.silverhetch.clotho.Source

/**
 * Adapter to convert the raw entity into client data view.
 */
class DiaryListView(private val diaries: List<RDiary>) : Source<JsonArray> {
    override fun value(): JsonArray {
        return JsonArray().apply {
            diaries.forEach {
                add(DiaryView(it).value())
            }
        }
    }
}