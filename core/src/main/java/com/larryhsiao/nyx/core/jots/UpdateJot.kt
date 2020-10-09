package com.larryhsiao.nyx.core.jots

import com.silverhetch.clotho.Action
import com.silverhetch.clotho.Source
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.impl.CoordinateArraySequence
import java.sql.Connection
import java.sql.SQLException
import java.sql.Timestamp
import java.util.*

/**
 * Action to update given Jot
 */
class UpdateJot @JvmOverloads constructor(
    private val updated: Jot,
    private val connSource: Source<Connection>,
    private val increaseVer: Boolean = true
) : Action {
    override fun fire() {
        val conn = connSource.value()
        try {
            conn.prepareStatement( // language=H2
                """// --
UPDATE jots
SET content=?1,
    location=?2,
    CREATEDTIME=?3,
    MOOD=?4,
    VERSION=?5,
    DELETE=?7,
    TITLE=?8,
    PRIVATE=?9
WHERE id = ?6;"""
            ).use { stmt ->
                stmt.setString(1, updated.content())
                stmt.setString(2, Point(
                    CoordinateArraySequence(arrayOf(
                        Coordinate(
                            updated.location()[0],
                            updated.location()[1]
                        )
                    )), GeometryFactory()
                ).toText())
                stmt.setTimestamp(3, Timestamp(updated.createdTime()), Calendar.getInstance())
                val mood = updated.mood()
                if (mood.length > 1) {
                    stmt.setString(4, mood.substring(0, 2))
                } else {
                    stmt.setString(4, "")
                }
                stmt.setInt(5, if (increaseVer) updated.version() + 1 else updated.version())
                stmt.setLong(6, updated.id())
                stmt.setInt(7, if (updated.deleted()) 1 else 0)
                stmt.setString(8, updated.title())
                stmt.setBoolean(9, updated.privateLock())
                stmt.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}