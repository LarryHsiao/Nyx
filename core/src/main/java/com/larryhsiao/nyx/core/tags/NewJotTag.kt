package com.larryhsiao.nyx.core.tags

import com.silverhetch.clotho.Action
import com.silverhetch.clotho.Source
import java.sql.Connection

/**
 * Action to mark a Tag as jot's tag.
 */
class NewJotTag(
    private val conSource: Source<Connection>,
    private val jotId: Source<Long>,
    private val tagId: Source<Long>
) : Action {
    override fun fire() {
        try {
            val conn = conSource.value()
            val queryTagJot = conn.prepareStatement( // language=H2
                "SELECT * FROM  TAG_JOT WHERE TAG_ID=?2 AND JOT_ID=?1"
            )
            queryTagJot.setLong(1, jotId.value())
            queryTagJot.setLong(2, tagId.value())
            val tagJotRes = queryTagJot.executeQuery()
            val jotTagExist = tagJotRes.next()
            queryTagJot.close()
            if (jotTagExist) {
                val stmt = conn.prepareStatement( // language=H2
                    "UPDATE TAG_JOT " +
                        "SET VERSION = VERSION + 1, DELETE=0 " +
                        "WHERE JOT_ID=?1 AND TAG_ID=?2;"
                )
                stmt.setLong(1, jotId.value())
                stmt.setLong(2, tagId.value())
                stmt.executeUpdate()
                stmt.close()
            } else {
                val stmt = conn.prepareStatement( // language=H2
                    "INSERT INTO TAG_JOT(jot_id, tag_id) VALUES ( ?,? )"
                )
                stmt.setLong(1, jotId.value())
                stmt.setLong(2, tagId.value())
                stmt.executeUpdate()
                stmt.close()
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}