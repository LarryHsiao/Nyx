package com.larryhsiao.nyx.core.jots

import com.silverhetch.clotho.Source
import org.locationtech.jts.geom.Geometry
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

/**
 * Source to find jots by given geometry polygon.
 */
class JotsByLocation(
    private val conn: Source<Connection>,
    private val geometry: Geometry
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            val stmt = conn.value().prepareStatement( // language=H2
                "SELECT * FROM jots " +
                    "WHERE location && ? " +
                    "AND DELETE = 0 " +
                    "ORDER BY CREATEDTIME DESC;"
            )
            stmt.setString(1, geometry.toText())
            stmt.executeQuery()
        } catch (e: SQLException) {
            throw IllegalArgumentException(e)
        }
    }
}