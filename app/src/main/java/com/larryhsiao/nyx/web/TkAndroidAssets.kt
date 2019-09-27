package com.larryhsiao.nyx.web

import android.content.Context
import com.google.api.client.http.HttpStatusCodes.STATUS_CODE_OK
import com.silverhetch.clotho.io.ProgressedCopy
import org.takes.Request
import org.takes.Response
import org.takes.Take
import org.takes.rs.RsWithBody
import org.takes.rs.RsWithStatus
import org.takes.rs.RsWithType
import java.io.ByteArrayOutputStream

/**
 * Take for index file in Android frameworks assets folder.
 */
class TkAndroidAssets(
    private val context: Context,
    private val host: String,
    private val path: String,
    private val mimeType: String
) :
    Take {
    override fun act(req: Request?): Response {
        val body = ByteArrayOutputStream()
        ProgressedCopy(
            context.assets.open(path),
            body
        ) {}.value()
        return RsWithBody(
            RsWithType(
                RsWithStatus(STATUS_CODE_OK), mimeType
            ),
            body.toString()
        )
    }
}