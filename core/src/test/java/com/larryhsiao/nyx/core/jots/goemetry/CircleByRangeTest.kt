package com.larryhsiao.nyx.core.jots.goemetry

import com.larryhsiao.nyx.core.jots.JotsByTimeSpace
import com.larryhsiao.nyx.core.jots.JotsDb
import com.larryhsiao.nyx.core.jots.NewJot
import com.larryhsiao.nyx.core.jots.QueriedJots
import com.silverhetch.clotho.Source
import com.silverhetch.clotho.database.h2.MemoryH2Conn
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Connection

/**
 * Unit-test for the class [CircleByRange]
 */
internal class CircleByRangeTest {
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
            CircleByRange(doubleArrayOf(0.0, 0.0), MeterDelta())
        )).value()
        Assertions.assertNotEquals(0, jots.size)
    }
}