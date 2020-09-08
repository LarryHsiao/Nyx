package com.larryhsiao.nyx.core.tags

import com.silverhetch.clotho.Source
import java.sql.Connection
import java.sql.Statement

/**
 * Source to build a Tag that just created.
 */
class NewTag(private val connSource: Source<Connection>, private val tag: Tag) : Source<Tag?> {
    constructor(
        connSource: Source<Connection>,
        title: String?
    ) : this(connSource, ConstTag(-1L, title, 1, false)) {
    }

    override fun value(): Tag {
        try {
            connSource.value().prepareStatement( // language=H2
                "INSERT INTO TAGS (TITLE)VALUES ( ? );", Statement.RETURN_GENERATED_KEYS
            ).use { stmt ->
                stmt.setString(1, tag.title())
                stmt.executeUpdate()
                val res = stmt.generatedKeys
                require(res.next()) { "Creating tag failed, title: " + tag.title() }
                val newId = res.getLong(1)
                return object : WrappedTag(tag) {
                    override fun id(): Long {
                        return newId
                    }
                }
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}