package com.larryhsiao.nyx.core.jots

import com.larryhsiao.clotho.Source
import org.locationtech.jts.io.WKTReader
import java.sql.ResultSet
import java.util.*

/**
 * Adapter to adapt query result to Jot objects.
 */
class QueriedJots(private val query: Source<ResultSet>) : Source<List<Jot?>?> {
    override fun value(): List<Jot> {
        try {
            query.value().use { res ->
                val jots: MutableList<Jot> = ArrayList()
                while (res.next()) {
                    toJot(res, jots)
                }
                return jots
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }

    @Throws(Exception::class)
    private fun toJot(res: ResultSet, jots: MutableList<Jot>) {
        val timestamp = res.getTimestamp(
            res.findColumn("createdTime"),
            Calendar.getInstance())
        val locationStr = res.getString("location")
        var location = doubleArrayOf(Double.MIN_VALUE, Double.MIN_VALUE)
        if (locationStr != null) {
            val locationGeo = WKTReader().read(locationStr).centroid
            location = doubleArrayOf(locationGeo.x, locationGeo.y)
        }
        jots.add(ConstJot(
            res.getLong(res.findColumn("id")),
            res.getString(res.findColumn("title")),
            res.getString(res.findColumn("content")),
            timestamp.time,
            location,
            res.getString("mood"),
            res.getInt("version"),
            res.getInt("delete") == 1,
            res.getBoolean("private")
        ))
    }
}