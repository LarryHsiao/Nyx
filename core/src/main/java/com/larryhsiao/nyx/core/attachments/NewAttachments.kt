package com.larryhsiao.nyx.core.attachments

import com.silverhetch.clotho.Source
import java.sql.Connection
import java.util.*

/**
 * Create multiple attachments
 */
class NewAttachments(
    private val connSource: Source<Connection>,
    private val jotId: Long,
    private val uris: Array<String>
) : Source<List<Attachment?>?> {
    override fun value(): List<Attachment?> {
        val res: MutableList<Attachment?> = ArrayList()
        for (uri in uris) {
            res.add(NewAttachment(
                connSource,
                uri,
                jotId
            ).value())
        }
        return res
    }
}