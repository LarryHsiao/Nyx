package com.larryhsiao.nyx.core.tags

import com.silverhetch.clotho.Source
import java.sql.Connection

/**
 * Source to build a Tag that just created.
 */
class NewTagById(private val connSource: Source<Connection>, private val tag: Tag) : Source<Tag?> {
    override fun value(): Tag {
        try {
            connSource.value().prepareStatement( // language=H2
                "INSERT INTO TAGS (ID, TITLE, VERSION, DELETE)VALUES ( ?, ?, ?, ? );"
            ).use { stmt ->
                stmt.setLong(1, tag.id())
                stmt.setString(2, tag.title())
                stmt.setInt(3, tag.version())
                stmt.setInt(4, if (tag.deleted()) 1 else 0)
                stmt.executeUpdate()
                return tag
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}