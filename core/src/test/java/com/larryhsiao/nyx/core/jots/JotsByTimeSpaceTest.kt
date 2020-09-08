package com.larryhsiao.nyx.core.jots

import com.silverhetch.clotho.Source
import com.silverhetch.clotho.database.h2.MemoryH2Conn
import com.silverhetch.clotho.source.ConstSource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.*
import org.locationtech.jts.geom.impl.CoordinateArraySequence
import java.sql.Connection

/**
 * Unit-test for the class [JotsByTimeSpace]
 */
internal class JotsByTimeSpaceTest {
    /**
     * Check search by geometry works.
     */
    @Test
    @Throws(Exception::class)
    fun normalCase() {
        val db: Source<Connection> = JotsDb(MemoryH2Conn())
        NewJot(db, "title", "content", doubleArrayOf(0.0, 0.0), "").value()
        val jots = QueriedJots(JotsByTimeSpace(
            db,
            System.currentTimeMillis(),
            includedGeometry()
        )).value()
        Assertions.assertNotEquals(
            0,
            jots[0].createdTime()
        )
    }

    /**
     * When the time is not match.
     */
    @Test
    @Throws(Exception::class)
    fun timeNotMatch() {
        val db: Source<Connection> = JotsDb(MemoryH2Conn())
        NewJot(db, "title", "content", doubleArrayOf(0.0, 0.0), "").value()
        val jots = QueriedJots(JotsByTimeSpace(
            db,
            System.currentTimeMillis() + 300001,
            includedGeometry()
        )).value()
        Assertions.assertEquals(0, jots.size)
    }

    /**
     * When the geometry not match.
     */
    @Test
    @Throws(Exception::class)
    fun geometryNotMatch() {
        val db: Source<Connection> = JotsDb(MemoryH2Conn())
        NewJot(db, "title", "content", doubleArrayOf(0.0, 0.0), "").value()
        val jots = QueriedJots(JotsByTimeSpace(
            db,
            System.currentTimeMillis(),
            notIncludedGeometry()
        )).value()
        Assertions.assertEquals(0, jots.size)
    }

    private fun includedGeometry(): Source<Geometry> {
        return ConstSource(Polygon(
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
        ))
    }

    private fun notIncludedGeometry(): Source<Geometry> {
        return ConstSource(Polygon(
            LinearRing(
                CoordinateArraySequence(arrayOf(
                    Coordinate(3.0, 3.0),
                    Coordinate(3.0, 1.0),
                    Coordinate(1.0, 1.0),
                    Coordinate(1.0, 3.0),
                    Coordinate(3.0, 3.0)
                )), GeometryFactory()
            ), arrayOfNulls(0),
            GeometryFactory()
        ))
    }
}