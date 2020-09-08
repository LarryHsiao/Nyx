package com.larryhsiao.nyx.thirdparty

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.impl.CoordinateArraySequence

/**
 * Geometry string related
 */
class GeometryStringTest {
    /**
     * Generate geometry string
     */
    @Test
    fun geometryString() {
        Assertions.assertEquals(
            "POINT (100.5 90.5)",
            Point(
                CoordinateArraySequence(arrayOf(
                    Coordinate(100.5, 90.5)
                )),
                GeometryFactory()
            ).toText()
        )
    }
}