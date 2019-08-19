package com.larryhsiao.nyx.web.files

import com.google.api.client.http.HttpStatusCodes.STATUS_CODE_NO_CONTENT
import com.silverhetch.clotho.io.ProgressedCopy
import org.takes.Request
import org.takes.Response
import org.takes.Take
import org.takes.rq.RqHref
import org.takes.rq.multipart.RqMtSmart
import org.takes.rs.RsWithHeader
import org.takes.rs.RsWithStatus
import java.io.File
import java.io.FileOutputStream

/**
 * Take to upload file to the device
 */
class TkFileUpload(private val storageRoot: File) : Take {
    override fun act(req: Request?): Response {
        val multiReq = RqMtSmart(req)
        val targetFile =
            File(
                storageRoot,
                multiReq.head().find {
                    it.startsWith("fileName")
                }?.split(": ")?.last() ?: ""
            )
        ProgressedCopy(
            multiReq.single("file").body(),
            FileOutputStream(targetFile)
        ) {}.value()
        return RsWithHeader(
            RsWithStatus(STATUS_CODE_NO_CONTENT),
            "Location",
            RqHref
                .Smart(req).href().toString() + "/${targetFile.name}"
        )
    }
}