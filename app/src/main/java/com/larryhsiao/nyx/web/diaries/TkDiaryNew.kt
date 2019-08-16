package com.larryhsiao.nyx.web.diaries

import com.google.api.client.http.HttpStatusCodes.*
import com.google.gson.Gson
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.NewDiary
import com.larryhsiao.nyx.diary.room.DiaryEntity
import org.takes.Request
import org.takes.Response
import org.takes.Take
import org.takes.rq.RqHref
import org.takes.rq.RqPrint
import org.takes.rs.RsWithHeader
import org.takes.rs.RsWithStatus
import java.io.StringReader

/**
 * Create a new diary.
 */
class TkDiaryNew(private val db: RDatabase) : Take {
    override fun act(req: Request?): Response {
        return req?.let { req ->
            val id = db.diaryDao().create(
                Gson().fromJson(
                    StringReader(RqPrint(req).printBody()),
                    DiaryEntity::class.java
                )
            )
            RsWithStatus(
                RsWithHeader(
                    "Location",
                    RqHref
                        .Smart(req).href().toString() + "/$id"
                ),
                STATUS_CODE_CREATED
            )
        } ?: RsWithStatus(STATUS_CODE_BAD_REQUEST)
    }

}
