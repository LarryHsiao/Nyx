package com.larryhsiao.nyx.core.jots;

import com.larryhsiao.nyx.core.jots.filter.Filter;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Source to select implementation to run with.
 */
public class JotsByCheckedFilter implements Source<ResultSet> {
    private final Source<Connection> db;
    private final Filter filter;

    public JotsByCheckedFilter(Source<Connection> db, Filter filter) {
        this.db = db;
        this.filter = filter;
    }

    @Override
    public ResultSet value() {
        long[] range = filter.dateRange();
        if (range[0] == 0L && range[1] == 0L) {
            return new JotsByKeyword(db, filter.keyword()).value();
        }
        return new JotsByFilter(db, filter).value();
    }
}
