package com.larryhsiao.nyx.thirdparty;

import org.junit.Assert;
import org.junit.Test;
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
        Assert.assertEquals(
                "",
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
