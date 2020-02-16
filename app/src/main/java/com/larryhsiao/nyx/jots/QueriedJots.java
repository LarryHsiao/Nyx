package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Source;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
        try (ResultSet res = query.value()){
            List<Jot> jots = new ArrayList<>();
            ;
            while (res.next()) {
                Timestamp timestamp = res.getTimestamp(
                        res.findColumn("createdTime"),
                        Calendar.getInstance());
                jots.add(new ConstJot(
                        res.getLong(res.findColumn("id")),
                        res.getString(res.findColumn("content")),
                        timestamp.getTime()
                ));
            }
            return jots;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
