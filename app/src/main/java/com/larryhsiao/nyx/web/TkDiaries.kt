package com.larryhsiao.nyx.web

import com.google.gson.Gson
import com.larryhsiao.nyx.database.RDatabase
import org.takes.Request
import org.takes.Response
import org.takes.Take

/**
 * Fork of response Diary resources in json.
 */
class TkDiaries(private val db: RDatabase) : Take {
    override fun act(req: Request?): Response {
        val diaries = db.diaryDao().all()
        return RsJson(Gson().toJson(Array(diaries.size){
            diaries[it].diary
        }))
    }
}