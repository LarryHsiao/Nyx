package com.larryhsiao.nyx.core.jots

import com.silverhetch.clotho.Source
import com.silverhetch.clotho.database.h2.MemoryH2Conn
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LinearRing
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.geom.impl.CoordinateArraySequence
import java.sql.Connection

/**
 * Unit-test for the class [JotsByLocation]
 */
class JotsByLocationTest {
    /**
     * Check search by geometry works.
     */
    @Test
    @Throws(Exception::class)
    fun createdTimeExist() {
        val db: Source<Connection> = JotsDb(MemoryH2Conn())
        NewJot(db, "title", "content", doubleArrayOf(0.0, 0.0), "").value()
        val jots = QueriedJots(
            JotsByLocation(
                db, Polygon(
                LinearRing(
                    CoordinateArraySequence(arrayOf(
                        Coordinate(1.0, 1.0),
                        Coordinate(1.0, -1.0),
                        Coordinate(-1.0, -1.0),
                        Coordinate(-1.0, 1.0),
                        Coordinate(1.0, 1.0)
                    )), GeometryFactory()
                ), arrayOfNulls(0),
                GeometryFactory()
            ))).value()
        Assertions.assertNotEquals(
            0,
            jots[0].createdTime()
        )
    }
}