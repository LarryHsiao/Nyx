package com.larryhsiao.nyx.core.tags;

import com.larryhsiao.clotho.Source;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Source to build List of JotTag from SQL query result.
 */
public class QueriedJotTags implements Source<List<JotTag>> {
    private final Source<ResultSet> resSrc;

    public QueriedJotTags(Source<ResultSet> resSrc) {
        this.resSrc = resSrc;
    }

    @Override
    public List<JotTag> value() {
        try (ResultSet resultSet = resSrc.value()) {
            final List<JotTag> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(new ConstJotTag(
                    resultSet.getLong("jot_id"),
                    resultSet.getLong("tag_id"),
                    resultSet.getInt("delete") == 1,
                    resultSet.getInt("version")
                ));
            }
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
