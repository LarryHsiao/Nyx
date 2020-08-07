package com.larryhsiao.nyx.core.jots.goemetry;

import com.silverhetch.clotho.Source;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

/**
 * Source to Build a {@link Geometry} of circle from given point as circle center
 * and distance as radius.
 */
public class CircleByRange implements Source<Geometry> {
    private final double[] lngLat;
    private final Source<Double> distanceDelta;

    public CircleByRange(double[] lngLat, Source<Double> distanceDelta) {
        this.lngLat = lngLat;
        this.distanceDelta = distanceDelta;
    }

    @Override
    public Geometry value() {
        double distance = distanceDelta.value();
        return new Polygon(
            new LinearRing(
                new CoordinateArraySequence(
                    new Coordinate[]{
                        new Coordinate(lngLat[0] + distance, lngLat[1] + distance),
                        new Coordinate(lngLat[0] + distance, lngLat[1] - distance),
                        new Coordinate(lngLat[0] - distance, lngLat[1] - distance),
                        new Coordinate(lngLat[0] - distance, lngLat[1] + distance),
                        new Coordinate(lngLat[0] + distance, lngLat[1] + distance),
                    }
                ), new GeometryFactory()
            ),
            new LinearRing[0],
            new GeometryFactory()
        );
    }
}
