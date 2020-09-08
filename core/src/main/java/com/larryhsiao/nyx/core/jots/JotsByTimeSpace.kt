package com.larryhsiao.nyx.core.jots

import com.silverhetch.clotho.Source
import org.locationtech.jts.geom.Geometry
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp

/**
 * Source to build [ResultSet] for querying result by time and geometry.
 */
class JotsByTimeSpace(
    private val db: Source<Connection>,
    private val time: Long,
    private val geometry: Source<Geometry>
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            val stmt = db.value().prepareStatement( // language=H2
                "SELECT * FROM jots " +
                    "WHERE location && ? AND CREATEDTIME > ?" +
                    "AND DELETE = 0 " +
                    "ORDER BY CREATEDTIME DESC;"
            )
            stmt.setString(1, geometry.value().toText())
            stmt.setTimestamp(2, Timestamp(time - 300000)) // 5 min range
            stmt.executeQuery()
        } catch (e: SQLException) {
            throw IllegalArgumentException(e)
        }
    }
}