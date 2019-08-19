package com.larryhsiao.nyx.web.diaries

import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.web.RsJson
import org.takes.Request
import org.takes.Response
import org.takes.Take
import org.takes.rq.RqHref

/**
 * Get diary by Id.
 */
class TkDiaryById(private val db: RDatabase) : Take {
    override fun act(req: Request?): Response {
        val id = RqHref.Smart(req).href().path().split('/').last()
        return RsJson(DiaryListView(listOf(db.diaryDao().byId(id.toLong()))).value().toString())
    }

}
