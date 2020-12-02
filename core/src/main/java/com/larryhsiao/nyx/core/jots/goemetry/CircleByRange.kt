package com.larryhsiao.nyx.core.jots.goemetry

import com.larryhsiao.clotho.Source
import org.locationtech.jts.geom.*
import org.locationtech.jts.geom.impl.CoordinateArraySequence

/**
 * Source to Build a [Geometry] of circle from given point as circle center
 * and distance as radius.
 */
class CircleByRange(
    private val lngLat: DoubleArray,
    private val distanceDelta: Source<Double>
) : Source<Geometry> {
    override fun value(): Geometry {
        val distance = distanceDelta.value()
        return Polygon(
            LinearRing(
                CoordinateArraySequence(arrayOf(
                    Coordinate(lngLat[0] + distance, lngLat[1] + distance),
                    Coordinate(lngLat[0] + distance, lngLat[1] - distance),
                    Coordinate(lngLat[0] - distance, lngLat[1] - distance),
                    Coordinate(lngLat[0] - distance, lngLat[1] + distance),
                    Coordinate(lngLat[0] + distance, lngLat[1] + distance))), GeometryFactory()
            ), arrayOfNulls(0),
            GeometryFactory()
        )
    }
}