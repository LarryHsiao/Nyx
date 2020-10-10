package com.larryhsiao.nyx.core.jots

import com.silverhetch.clotho.Source
import java.sql.Connection
import java.sql.SQLException
import java.util.*

/**
 * Source to build jot by Id.
 */
class JotById(private val id: Long, private val db: Source<Connection>) : Source<Jot?> {
    override fun value(): Jot {
        try {
            db.value().prepareStatement( // language=H2
                "SELECT * FROM jots WHERE id=?;"
            ).use { stmt ->
                stmt.setLong(1, id)
                val res = stmt.executeQuery()
                require(res.next()) { "Jot not found, id: $id" }
                return ConstJot(
                    res.getLong("id"),
                    res.getString("title"),
                    res.getString("content"),
                    res.getTimestamp(
                        "createdTime",
                        Calendar.getInstance()
                    ).time,
                    PointSource(res.getString("location")).value(),
                    res.getString("mood"),
                    res.getInt("version"),
                    res.getInt("delete") == 1,
                    res.getBoolean("private")
                )
            }
        } catch (e: SQLException) {
            throw IllegalArgumentException(e)
        }
    }
}