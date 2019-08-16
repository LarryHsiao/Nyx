package com.larryhsiao.nyx.web.files

import com.google.api.client.http.HttpStatusCodes.STATUS_CODE_OK
import org.takes.Request
import org.takes.Response
import org.takes.Take
import org.takes.rq.RqHref
import org.takes.rs.RsWithBody
import org.takes.rs.RsWithStatus
import java.io.File
import java.io.FileInputStream

/**
 * Download the file by given id
 */
class TkFileById(private val fileRoot: File) : Take {
    override fun act(req: Request?): Response {
        val id = RqHref.Smart(req).href().path().split('/').last()
        return RsWithBody(
            RsWithStatus(STATUS_CODE_OK),
            FileInputStream(File(fileRoot, id))
        )
    }
}