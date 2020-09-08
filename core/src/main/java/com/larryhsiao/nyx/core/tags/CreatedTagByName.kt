package com.larryhsiao.nyx.core.tags

import com.silverhetch.clotho.Source
import java.sql.Connection

/**
 * Source to build a Tag that have a row in the db.
 */
class CreatedTagByName(
    private val db: Source<Connection>,
    private val title: String
) : Source<Tag?> {
    override fun value(): Tag {
        return try {
            val con = db.value()
            val stmt = con.prepareStatement( // language=H2
                "SELECT * FROM TAGS WHERE TITLE=?1"
            )
            stmt.setString(1, title)
            val res = stmt.executeQuery()
            if (res.next()) {
                return ConstTag(
                    res.getLong("id"),
                    res.getString("title"),
                    res.getInt("version"),
                    res.getInt("delete") == 1
                )
            }
            res.close()
            stmt.close()
            NewTag(db, title).value()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}