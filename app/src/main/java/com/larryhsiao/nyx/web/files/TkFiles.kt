package com.larryhsiao.nyx.web.files

import com.google.api.client.http.HttpStatusCodes.STATUS_CODE_OK
import com.google.gson.Gson
import com.larryhsiao.nyx.web.RsJson
import org.takes.Request
import org.takes.Response
import org.takes.Take
import org.takes.rs.RsWithStatus
import java.io.File

/**
 * Take to response the current files in the given file root.
 */
class TkFiles(private val root: File) : Take {
    override fun act(req: Request?): Response {
        val files = root.listFiles()
        return RsWithStatus(
            RsJson(Gson().toJson(Array<String>(files.size) {
                files[it].name
            })),
            STATUS_CODE_OK
        )
    }
}