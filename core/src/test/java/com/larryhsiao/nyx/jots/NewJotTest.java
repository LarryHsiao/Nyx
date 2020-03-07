package com.larryhsiao.nyx.jots;

import com.larryhsiao.nyx.core.jots.AllJots;
import com.larryhsiao.nyx.core.jots.JotsDb;
import com.larryhsiao.nyx.core.jots.NewJot;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.h2.MemoryH2Conn;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Unit-test for the class {@link NewJot}
 */
public class NewJotTest {
    /**
     * Check insert success.
     */
    @Test
    public void simple() throws SQLException {
        Source<Connection> db = new JotsDb(new MemoryH2Conn());
        new NewJot(db, "content").value();
        try (ResultSet res = new AllJots(db).value()) {
            int count = 0;
            while (res.next()){
                count++;
            }
            Assertions.assertEquals(1, count);
        }
    }
}