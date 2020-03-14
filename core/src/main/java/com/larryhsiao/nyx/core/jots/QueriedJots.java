package com.larryhsiao.nyx.core.jots;

import com.silverhetch.clotho.Source;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Double.MIN_VALUE;

/**
 * Adapter to adapt query result to Jot objects.
 */
public class QueriedJots implements Source<List<Jot>> {
    private final Source<ResultSet> query;

    public QueriedJots(Source<ResultSet> query) {
        this.query = query;
    }

    @Override
    public List<Jot> value() {
        try (ResultSet res = query.value()) {
            List<Jot> jots = new ArrayList<>();
            while (res.next()) {
                toJot(res, jots);
            }
            return jots;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void toJot(ResultSet res, List<Jot> jots) throws Exception {
        Timestamp timestamp = res.getTimestamp(
            res.findColumn("createdTime"),
            Calendar.getInstance());
        String locationStr = res.getString("location");
        double[] location = new double[]{MIN_VALUE, MIN_VALUE};
        if (locationStr != null) {
            final Point locationGeo = new WKTReader().read(locationStr).getCentroid();
            location = new double[]{locationGeo.getX(), locationGeo.getY()};
        }
        jots.add(new ConstJot(
            res.getLong(res.findColumn("id")),
            res.getString(res.findColumn("content")),
            timestamp.getTime(),
            location,
            res.getString("mood"),
            res.getInt("version"),
            res.getInt("delete") == 1
        ));
    }
}
