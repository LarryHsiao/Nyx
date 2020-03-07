package com.larryhsiao.nyx.core.tags;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Source to build tags by Jot id.
 */
public class TagsByKeyword implements Source<ResultSet> {
    private final Source<Connection> connSource;
    private final String keyword;

    public TagsByKeyword(Source<Connection> connSource, String keyword) {
        this.connSource = connSource;
        this.keyword = keyword;
    }

    @Override
    public ResultSet value() {
        try {
            PreparedStatement stmt = connSource.value().prepareStatement(
                //language=H2
                "SELECT  * FROM TAGS " +
                    "WHERE TITLE LIKE ?;"
            );
            stmt.setString(1, "%" + keyword +"%");
            return stmt.executeQuery();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
