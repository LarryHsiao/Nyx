package com.larryhsiao.nyx.web.media

import com.google.gson.Gson
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.web.RsJson
import org.takes.Request
import org.takes.Response
import org.takes.Take

/**
 * Take that returns medias recorded in database.
 */
class TkMedia(
    private val db: RDatabase
) : Take {
    override fun act(req: Request?): Response {
        val media = db.mediaDao().all()
        return RsJson(Gson().toJson(Array(media.size) {
            media[it]
        }))
    }
}