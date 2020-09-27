package com.larryhsiao.nyx.core.jots

import com.silverhetch.clotho.Source
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.impl.CoordinateArraySequence
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import java.sql.Statement.RETURN_GENERATED_KEYS
import java.sql.Timestamp
import java.util.*

/**
 * Source to build a Jot which just created by user.
 */
class NewJot : Source<Jot> {
    private val db: Source<Connection>
    private val jot: Jot

    constructor(
        db: Source<Connection>,
        title: String,
        content: String,
        calendar: Calendar,
        mood: String
    ) : this(db, title, content, doubleArrayOf(Double.MIN_VALUE, Double.MIN_VALUE), calendar, mood) {
    }

    constructor(
        db: Source<Connection>,
        title: String,
        content: String,
        longLat: DoubleArray,
        mood: String
    ) : this(db, title, content, longLat, Calendar.getInstance(), mood) {
    }

    @JvmOverloads
    constructor(
        db: Source<Connection>,
        title: String,
        content: String,
        longLat: DoubleArray = doubleArrayOf(Double.MIN_VALUE, Double.MIN_VALUE),
        calendar: Calendar = Calendar.getInstance(),
        mood: String = " "
    ) {
        this.db = db
        jot = ConstJot(
            -1L,
            title,
            content,
            calendar.timeInMillis,
            longLat,
            mood,
            1,
            false
        )
    }

    constructor(db: Source<Connection>, jot: Jot) {
        this.db = db
        this.jot = jot
    }

    override fun value(): Jot {
        try {
            db.value().prepareStatement( // language=H2
                """// 
INSERT INTO jots(content, createdTime, location, mood, VERSION, TITLE)
VALUES (?, ?, ?, ?, ?, ?)""",
                RETURN_GENERATED_KEYS
            ).use { stmt ->
                stmt.setString(1, jot.content())
                stmt.setTimestamp(2, Timestamp(jot.createdTime()), Calendar.getInstance())
                val location = jot.location()
                stmt.setString(3, Point(
                    CoordinateArraySequence(arrayOf(
                        Coordinate(location[0], location[1])
                    )), GeometryFactory()
                ).toText())
                if (jot.mood().length > 1) {
                    stmt.setString(4, jot.mood().substring(0, 2))
                } else {
                    stmt.setString(4, "")
                }
                stmt.setInt(5, jot.version())
                stmt.setString(6, jot.title())
                if (stmt.executeUpdate() == 0) {
                    throw SQLException("Insert failed")
                }
                val res = stmt.generatedKeys
                require(res.next()) { "Create jot failed: " + jot.content() }
                val newId = res.getLong(1)
                return object : WrappedJot(jot) {
                    override fun id(): Long {
                        return newId
                    }
                }
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}