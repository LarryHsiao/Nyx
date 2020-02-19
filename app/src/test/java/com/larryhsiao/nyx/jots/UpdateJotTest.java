package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.h2.MemoryH2Conn;
import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.assertEquals;

/**
 * Unit-test for the class {@link UpdateJot}
 */
public class UpdateJotTest {

    /**
     * Check the jot updated in db
     */
    @Test
    public void simple() {
        final String newContent = "newContent";
        Source<Connection> db = new JotsDb(new MemoryH2Conn());
        Jot jot = new NewJot(db, "content").value();
        new UpdateJot(new UpdatedJot(jot, newContent), db).fire();
        assertEquals(
                newContent,
                new QueriedJots(new AllJots(db)).value().get(0).content());
    }
}