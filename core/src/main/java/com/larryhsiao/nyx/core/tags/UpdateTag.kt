package com.larryhsiao.nyx.core.tags

import com.silverhetch.clotho.Action
import com.silverhetch.clotho.Source
import java.sql.Connection

/**
 * Action to update a Tag.
 */
class UpdateTag @JvmOverloads constructor(
    private val db: Source<Connection>,
    private val tag: Tag,
    private val increaseVer: Boolean = true
) : Action {
    override fun fire() {
        val conn = db.value()
        try {
            conn.prepareStatement( //language=H2
                "UPDATE TAGS " +
                    "SET TITLE=?1, VERSION = ?4, delete=?2 " +
                    "WHERE ID=?3"
            ).use { stmt ->
                stmt.setString(1, tag.title())
                stmt.setInt(2, if (tag.deleted()) 1 else 0)
                stmt.setLong(3, tag.id())
                stmt.setInt(4, if (increaseVer) tag.version() + 1 else tag.version())
                stmt.executeUpdate()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}