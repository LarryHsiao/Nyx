package com.larryhsiao.nyx.core.jots;

import com.silverhetch.clotho.Source;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;

import static java.lang.Double.MIN_VALUE;

/**
 * Source to build a Point from given geometry string.
 */
public class PointSource implements Source<double[]> {
    private final String value;

    public PointSource(String value) {
        this.value = value;
    }

    @Override
    public double[] value() {
        try {
            Point point = new WKTReader().read(value).getCentroid();
            return new double[]{point.getX(), point.getY()};
        } catch (Exception e) {
            return new double[]{MIN_VALUE, MIN_VALUE};
        }
    }
}
