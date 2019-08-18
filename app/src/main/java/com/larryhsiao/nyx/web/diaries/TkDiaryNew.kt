package com.larryhsiao.nyx.web.diaries

import android.content.Context
import com.google.api.client.http.HttpStatusCodes.*
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.larryhsiao.nyx.Config
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.NewDiary
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.larryhsiao.nyx.media.NewMedia
import org.takes.Request
import org.takes.Response
import org.takes.Take
import org.takes.rq.RqHref
import org.takes.rq.RqPrint
import org.takes.rs.RsWithHeader
import org.takes.rs.RsWithStatus
import java.io.File
import java.io.StringReader

/**
 * Create a new diary.
 */
class TkDiaryNew(
    private val context: Context,
    private val config: Config,
    private val db: RDatabase
) :
    Take {
    override fun act(req: Request?): Response {
        return req?.let { req ->
            val bodyObj = JsonParser()
                .parse(RqPrint(req).printBody())
                .asJsonObject
            val id = NewDiary(
                context,
                db,
                bodyObj["title"].asString,
                bodyObj["timestamp"].asLong,
                ArrayList<String>().apply {
                    bodyObj["file"]?.let {
                        add(
                            File(
                                config.mediaRoot(),
                                it.asString
                            ).toURI().toASCIIString()
                        )
                    }
                }
            ).value().id()
            RsWithStatus(
                RsWithHeader(
                    "Location",
                    RqHref.Smart(req).href().toString() + "/$id"
                ),
                STATUS_CODE_CREATED
            )
        } ?: RsWithStatus(STATUS_CODE_BAD_REQUEST)
    }

}
