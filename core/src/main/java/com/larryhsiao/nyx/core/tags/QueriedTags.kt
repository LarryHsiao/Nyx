package com.larryhsiao.nyx.core.tags;

import com.silverhetch.clotho.Source;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Source to build Tag list from result set.
 */
public class QueriedTags implements Source<List<Tag>> {
    private final Source<ResultSet> resSource;

    public QueriedTags(Source<ResultSet> resSource) {
        this.resSource = resSource;
    }

    @Override
    public List<Tag> value() {
        try (ResultSet res = resSource.value()) {
            List<Tag> tags = new ArrayList<>();
            while (res.next()) {
                tags.add(new ConstTag(
                    res.getLong("id"),
                    res.getString("title"),
                    res.getInt("version"),
                    res.getInt("delete") == 1)
                );
            }
            return tags;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}

