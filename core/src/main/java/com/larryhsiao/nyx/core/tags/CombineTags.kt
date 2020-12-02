package com.larryhsiao.nyx.core.tags

import com.larryhsiao.nyx.core.jots.Jot
import com.larryhsiao.nyx.core.jots.QueriedJots
import com.larryhsiao.clotho.Action
import com.larryhsiao.clotho.Source
import com.larryhsiao.clotho.source.ConstSource
import java.sql.Connection

/**
 * Action to combine two of tags.
 */
class CombineTags(
    private val db: Source<Connection>,
    private val targetId: Long,
    private val combinedId: Long
) : Action {
    override fun fire() {
        try {
            db.value().prepareStatement( // language=H2
                "UPDATE tag_jot "
                    + "SET TAG_ID=?1, VERSION=VERSION+1 "
                    + "WHERE TAG_ID=?2 AND JOT_ID NOT IN (" + targetJotIds() + ");"
            ).use { stmt ->
                stmt.setLong(1, targetId)
                stmt.setLong(2, combinedId)
                stmt.executeUpdate()
                TagRemoval(db, combinedId).fire()
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun targetJotIds(): String {
        val jots: Collection<Jot> = QueriedJots(
            JotsByTagId(db, ConstSource(targetId))
        ).value()
        val builder = StringBuilder()
        var index = 0
        for (jot in jots) {
            if (index > 0) {
                builder.append(", ")
            }
            builder.append(jot.id())
            index++
        }
        return builder.toString()
    }
}