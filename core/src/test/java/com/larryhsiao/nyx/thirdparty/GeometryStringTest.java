package com.larryhsiao.nyx.thirdparty;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

/**
 * Geometry string related
 */
public class GeometryStringTest {
    /**
     * Generate geometry string
     */
    @Test
    public void geometryString() {
        Assertions.assertEquals(
            "POINT (100.5 90.5)",
            new Point(
                new CoordinateArraySequence(
                    new Coordinate[]{
                        new Coordinate(100.5, 90.5)
                    }
                ),
                new GeometryFactory()
            ).toText()
        );
    }
}
