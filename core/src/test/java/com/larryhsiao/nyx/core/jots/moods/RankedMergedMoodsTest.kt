package com.larryhsiao.nyx.core.jots.moods

import com.larryhsiao.nyx.core.jots.JotsDb
import com.larryhsiao.nyx.core.jots.NewJot
import com.silverhetch.clotho.Source
import com.silverhetch.clotho.database.h2.MemoryH2Conn
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.util.*

/**
 * Unit-test for the class [RankedMoods]
 */
internal class RankedMergedMoodsTest {
    /**
     * Check the ranked moods size.
     */
    @Test
    fun simple() {
        val db: Source<Connection> = JotsDb(MemoryH2Conn())
        NewJot(db, "title", "content", Calendar.getInstance(), "12").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "34").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "56").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "78").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "90").value()
        Assertions.assertEquals(
            5,
            RankedMoods(db).value().size
        )
    }

    /**
     * Check the 1st ranked moods.
     */
    @Test
    fun rank1() {
        val db: Source<Connection> = JotsDb(MemoryH2Conn())
        NewJot(db, "title", "content", Calendar.getInstance(), "34").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "56").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "78").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "90").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "12").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "12").value()
        Assertions.assertEquals(
            "12",
            RankedMoods(db).value()[0].mood()
        )
    }

    /**
     * Check the 3rd ranked moods.
     */
    @Test
    fun rank3() {
        val db: Source<Connection> = JotsDb(MemoryH2Conn())
        NewJot(db, "title", "content", Calendar.getInstance(), "34").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "34").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "34").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "34").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "90").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "90").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "90").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "12").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "12").value()
        Assertions.assertEquals(
            "34",
            RankedMoods(db).value()[0].mood()
        )
    }

    /**
     * Check the ranked moods that all moods have same used count.
     */
    @Test
    fun rankSortingAllSame() {
        val db: Source<Connection> = JotsDb(MemoryH2Conn())
        NewJot(db, "title", "content", Calendar.getInstance(), "34").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "34").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "56").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "56").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "78").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "78").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "90").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "90").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "12").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "12").value()
        Assertions.assertEquals("12", RankedMoods(db).value()[0].mood())
        Assertions.assertEquals("34", RankedMoods(db).value()[1].mood())
        Assertions.assertEquals("56", RankedMoods(db).value()[2].mood())
    }

    /**
     * Check the ranked moods that actually have character inside.
     */
    @Test
    fun excludeEmpty() {
        val db: Source<Connection> = JotsDb(MemoryH2Conn())
        NewJot(db, "title", "content", Calendar.getInstance(), "  ").value()
        NewJot(db, "title", "content", Calendar.getInstance(), "").value()
        NewJot(db, "title", "content", Calendar.getInstance(), " ").value()
        Assertions.assertEquals(0, RankedMoods(db).value().size)
    }
}