package com.larryhsiao.nyx.core.jots

import com.larryhsiao.clotho.Source
import org.locationtech.jts.io.WKTReader

/**
 * Source to build a Point from given geometry string.
 */
class PointSource(private val value: String) : Source<DoubleArray?> {
    override fun value(): DoubleArray {
        return try {
            val point = WKTReader().read(value).centroid
            doubleArrayOf(point.x, point.y)
        } catch (e: Exception) {
            doubleArrayOf(Double.MIN_VALUE, Double.MIN_VALUE)
        }
    }
}