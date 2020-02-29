package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.h2.MemoryH2Conn;
import org.junit.Assert;
import org.junit.Test;
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
     * Check created time exist
     */
    @Test
    public void createdTimeExist() throws Exception {
        Source<Connection> db = new JotsDb(new MemoryH2Conn());
        new NewJot(db, "content",new double[]{0.0,0.0},' ').value();
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
        Assert.assertNotEquals(
            0,
            jots.get(0).createdTime()
        );
    }
}