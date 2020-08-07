package com.larryhsiao.nyx.core.jots.goemetry;

import com.larryhsiao.nyx.core.jots.*;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.h2.MemoryH2Conn;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

/**
 * Unit-test for the class {@link CircleByRange}
 */
class CircleByRangeTest {
    /**
     * Check search by geometry works.
     */
    @Test
    public void normalCase() throws Exception {
        Source<Connection> db = new JotsDb(new MemoryH2Conn());
        new NewJot(db, "content", new double[]{0.0, 0.0}, "").value();
        List<Jot> jots = new QueriedJots(new JotsByTimeSpace(
            db,
            System.currentTimeMillis(),
            new CircleByRange(new double[]{0, 0}, new MeterDelta())
        )).value();
        Assertions.assertNotEquals(0, jots.size());
    }
}