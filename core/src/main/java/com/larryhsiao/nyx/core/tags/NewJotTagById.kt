package com.larryhsiao.nyx.core.tags

import com.silverhetch.clotho.Action
import com.silverhetch.clotho.Source
import java.sql.Connection

/**
 * Action to mark a Tag as jot's tag.
 */
class NewJotTagById(
    private val conSource: Source<Connection>,
    private val jotId: Long,
    private val tagId: Long,
    private val version: Int,
    private val delete: Int
) : Action {
    override fun fire() {
        val conn = conSource.value()
        try {
            conn.prepareStatement( // language=H2
                "INSERT INTO TAG_JOT(jot_id, tag_id, version, delete) VALUES ( ?,?,?,? )"
            ).use { stmt ->
                stmt.setLong(1, jotId)
                stmt.setLong(2, tagId)
                stmt.setInt(3, version)
                stmt.setInt(4, delete)
                stmt.executeUpdate()
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}