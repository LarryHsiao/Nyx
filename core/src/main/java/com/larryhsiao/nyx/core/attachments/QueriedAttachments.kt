package com.larryhsiao.nyx.core.attachments

import com.silverhetch.clotho.Source
import java.sql.ResultSet
import java.util.*

/**
 * Source to build Attachment list from query source.
 */
class QueriedAttachments(private val query: Source<ResultSet>) : Source<List<Attachment>> {
    override fun value(): List<Attachment> {
        try {
            query.value().use { res ->
                val attachments: MutableList<Attachment> = ArrayList()
                while (res.next()) {
                    attachments.add(ConstAttachment(
                        res.getLong("id"),
                        res.getLong("jot_id"),
                        res.getString("uri"),
                        res.getInt("version"),
                        res.getInt("delete")
                    ))
                }
                return attachments
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}