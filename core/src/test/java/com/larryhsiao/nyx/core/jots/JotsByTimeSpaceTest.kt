package com.larryhsiao.nyx.core.jots;

import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.h2.MemoryH2Conn;
import com.silverhetch.clotho.source.ConstSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

import java.sql.Connection;
import java.util.List;

/**
 * Unit-test for the class {@link JotsByTimeSpace}
 */
class JotsByTimeSpaceTest {

    /**
     * Check search by geometry works.
     */
    @Test
    public void normalCase() throws Exception {
        Source<Connection> db = new JotsDb(new MemoryH2Conn());
        new NewJot(db, "title", "content", new double[]{0.0, 0.0}, "").value();
        List<Jot> jots = new QueriedJots(new JotsByTimeSpace(
            db,
            System.currentTimeMillis(),
            includedGeometry()
        )).value();
        Assertions.assertNotEquals(
            0,
            jots.get(0).createdTime()
        );
    }

    /**
     * When the time is not match.
     */
    @Test
    public void timeNotMatch() throws Exception {
        Source<Connection> db = new JotsDb(new MemoryH2Conn());
        new NewJot(db, "title", "content", new double[]{0.0, 0.0}, "").value();
        List<Jot> jots = new QueriedJots(new JotsByTimeSpace(
            db,
            System.currentTimeMillis() + 300001,
            includedGeometry()
        )).value();
        Assertions.assertEquals(0, jots.size());
    }

    /**
     * When the geometry not match.
     */
    @Test
    public void geometryNotMatch() throws Exception {
        Source<Connection> db = new JotsDb(new MemoryH2Conn());
        new NewJot(db, "title", "content", new double[]{0.0, 0.0}, "").value();
        List<Jot> jots = new QueriedJots(new JotsByTimeSpace(
            db,
            System.currentTimeMillis(),
            notIncludedGeometry()
        )).value();
        Assertions.assertEquals(0, jots.size());
    }

    private Source<Geometry> includedGeometry() {
        return new ConstSource<>(new Polygon(
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
        ));
    }

    private Source<Geometry> notIncludedGeometry() {
        return new ConstSource<>(new Polygon(
            new LinearRing(
                new CoordinateArraySequence(
                    new Coordinate[]{
                        new Coordinate(3.0, 3.0),
                        new Coordinate(3.0, 1.0),
                        new Coordinate(1.0, 1.0),
                        new Coordinate(1.0, 3.0),
                        new Coordinate(3.0, 3.0)
                    }
                ), new GeometryFactory()
            ),
            new LinearRing[0],
            new GeometryFactory()
        ));
    }
}