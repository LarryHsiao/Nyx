package com.larryhsiao.nyx.core.jots.moods;

import com.larryhsiao.nyx.core.jots.JotsDb;
import com.larryhsiao.nyx.core.jots.NewJot;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.h2.MemoryH2Conn;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Calendar;

/**
 * Unit-test for the class {@link RankedMoods}
 */
class RankedMergedMoodsTest {

    /**
     * Check the ranked moods size.
     */
    @Test
    void simple() {
        Source<Connection> db = new JotsDb(new MemoryH2Conn());
        new NewJot(db, "content", Calendar.getInstance(), "12").value();
        new NewJot(db, "content", Calendar.getInstance(), "34").value();
        new NewJot(db, "content", Calendar.getInstance(), "56").value();
        new NewJot(db, "content", Calendar.getInstance(), "78").value();
        new NewJot(db, "content", Calendar.getInstance(), "90").value();
        Assertions.assertEquals(
            5,
            new RankedMoods(db).value().size()
        );
    }

    /**
     * Check the 1st ranked moods.
     */
    @Test
    void rank1() {
        Source<Connection> db = new JotsDb(new MemoryH2Conn());
        new NewJot(db, "content", Calendar.getInstance(), "34").value();
        new NewJot(db, "content", Calendar.getInstance(), "56").value();
        new NewJot(db, "content", Calendar.getInstance(), "78").value();
        new NewJot(db, "content", Calendar.getInstance(), "90").value();
        new NewJot(db, "content", Calendar.getInstance(), "12").value();
        new NewJot(db, "content", Calendar.getInstance(), "12").value();
        Assertions.assertEquals(
            "12",
            new RankedMoods(db).value().get(0).mood()
        );
    }

    /**
     * Check the 3rd ranked moods.
     */
    @Test
    void rank3() {
        Source<Connection> db = new JotsDb(new MemoryH2Conn());
        new NewJot(db, "content", Calendar.getInstance(), "34").value();
        new NewJot(db, "content", Calendar.getInstance(), "34").value();
        new NewJot(db, "content", Calendar.getInstance(), "34").value();
        new NewJot(db, "content", Calendar.getInstance(), "34").value();
        new NewJot(db, "content", Calendar.getInstance(), "90").value();
        new NewJot(db, "content", Calendar.getInstance(), "90").value();
        new NewJot(db, "content", Calendar.getInstance(), "90").value();
        new NewJot(db, "content", Calendar.getInstance(), "12").value();
        new NewJot(db, "content", Calendar.getInstance(), "12").value();
        Assertions.assertEquals(
            "34",
            new RankedMoods(db).value().get(0).mood()
        );
    }
    /**
     * Check the ranked moods that all moods have same used count.
     */
    @Test
    void rankSortingAllSame() {
        Source<Connection> db = new JotsDb(new MemoryH2Conn());
        new NewJot(db, "content", Calendar.getInstance(), "34").value();
        new NewJot(db, "content", Calendar.getInstance(), "34").value();
        new NewJot(db, "content", Calendar.getInstance(), "56").value();
        new NewJot(db, "content", Calendar.getInstance(), "56").value();
        new NewJot(db, "content", Calendar.getInstance(), "78").value();
        new NewJot(db, "content", Calendar.getInstance(), "78").value();
        new NewJot(db, "content", Calendar.getInstance(), "90").value();
        new NewJot(db, "content", Calendar.getInstance(), "90").value();
        new NewJot(db, "content", Calendar.getInstance(), "12").value();
        new NewJot(db, "content", Calendar.getInstance(), "12").value();
        Assertions.assertEquals("12", new RankedMoods(db).value().get(0).mood());
        Assertions.assertEquals("34", new RankedMoods(db).value().get(1).mood());
        Assertions.assertEquals("56", new RankedMoods(db).value().get(2).mood());
    }

    /**
     * Check the ranked moods that actually have character inside.
     */
    @Test
    void excludeEmpty() {
        Source<Connection> db = new JotsDb(new MemoryH2Conn());
        new NewJot(db, "content", Calendar.getInstance(), "  ").value();
        new NewJot(db, "content", Calendar.getInstance(), "").value();
        new NewJot(db, "content", Calendar.getInstance(), " ").value();
        Assertions.assertEquals(0, new RankedMoods(db).value().size());
    }
}