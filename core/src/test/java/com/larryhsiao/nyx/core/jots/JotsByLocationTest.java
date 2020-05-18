package com.larryhsiao.nyx.core.jots;

import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.JotsByLocation;
import com.larryhsiao.nyx.core.jots.JotsDb;
import com.larryhsiao.nyx.core.jots.NewJot;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.h2.MemoryH2Conn;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

import java.sql.Connection;
import java.util.List;

/**
 * Unit-test for the class {@link JotsByLocation}
 */
public class JotsByLocationTest {
    /**
     * Check search by geometry works.
     */
    @Test
    public void createdTimeExist() throws Exception {
        Source<Connection> db = new JotsDb(new MemoryH2Conn());
        new NewJot(db, "content",new double[]{0.0,0.0},"").value();
        List<Jot> jots = new QueriedJots(
            new JotsByLocation(
                db, new Polygon(
                new LinearRing(
                    new CoordinateArraySequence(
                        new Coordinate[]{
                            new Coordinate(1.0, 1.0),
                            new Coordinate(1.0, -1.0),
                            new Coordinate(-1.0, -1.0),
                            new Coordinate(-1.0, 1.0),
                            new Coordinate(1.0, 1.0)
                        }
                    ), new GeometryFactory()
                ),
                new LinearRing[0],
                new GeometryFactory()
            ))).value();
        Assertions.assertNotEquals(
            0,
            jots.get(0).createdTime()
        );
    }
}