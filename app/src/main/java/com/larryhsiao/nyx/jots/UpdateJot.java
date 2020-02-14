package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Action to update given Jot
 */
public class UpdateJot implements Action {
    private final Jot updated;
    private final Source<Connection> connSource;

    public UpdateJot(Jot updated, Source<Connection> connSource) {
        this.updated = updated;
        this.connSource = connSource;
    }

    @Override
    public void fire() {
        final Connection conn = connSource.value();
        try (PreparedStatement stmt = conn.prepareStatement(
                // language=H2
                "UPDATE jots " +
                        "SET content=?1 " +
                        "WHERE id=?2;"
        )) {
            stmt.setString(1, updated.content());
            stmt.setLong(2, updated.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
