package com.larryhsiao.nyx.core.jots

import com.larryhsiao.clotho.Source
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.impl.CoordinateArraySequence
import java.sql.Connection
import java.sql.SQLException
import java.sql.Timestamp
import java.util.*

/**
 * Source to build a Jot which just created with specific Id.
 */
class NewJotById(private val db: Source<Connection>, private val jot: Jot) : Source<Jot?> {
    override fun value(): Jot {
        try {
            db.value().prepareStatement( // language=H2
                """
INSERT INTO jots(content, createdTime, location, mood, VERSION, ID, DELETE, TITLE, PRIVATE)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"""
            ).use { stmt ->
                stmt.setString(1, jot.content())
                stmt.setTimestamp(2, Timestamp(jot.createdTime()), Calendar.getInstance())
                val location = jot.location()
                if (location == null) {
                    stmt.setString(3, null)
                } else {
                    stmt.setString(3, Point(
                        CoordinateArraySequence(arrayOf(
                            Coordinate(location[0], location[1])
                        )), GeometryFactory()
                    ).toText())
                }
                if (jot.mood()!!.length > 1) {
                    stmt.setString(4, jot.mood()!!.substring(0, 2))
                } else {
                    stmt.setString(4, "")
                }
                stmt.setInt(5, jot.version())
                stmt.setLong(6, jot.id())
                stmt.setInt(7, if (jot.deleted()) 1 else 0)
                stmt.setString(8, jot.title())
                stmt.setBoolean(9, jot.privateLock())
                if (stmt.executeUpdate() == 0) {
                    throw SQLException("Insert failed")
                }
                return jot
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}