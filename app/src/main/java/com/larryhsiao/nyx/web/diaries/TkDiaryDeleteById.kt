package com.larryhsiao.nyx.web.diaries

import com.google.api.client.http.HttpStatusCodes.STATUS_CODE_NO_CONTENT
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.DiaryDeletion
import org.takes.Request
import org.takes.Response
import org.takes.Take
import org.takes.rq.RqHref
import org.takes.rs.RsWithStatus

/**
 * Delete the diary with given id.
 */
class TkDiaryDeleteById(private val db: RDatabase) : Take {
    override fun act(req: Request?): Response {
        val id = RqHref.Smart(req).href().path().split('/').last()
        DiaryDeletion(
            db,
            id.toLong()
        ).fire()
        return RsWithStatus(STATUS_CODE_NO_CONTENT)
    }

}
