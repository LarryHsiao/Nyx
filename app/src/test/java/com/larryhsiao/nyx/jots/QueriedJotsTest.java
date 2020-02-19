package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.h2.MemoryH2Conn;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

/**
 * Unit-test for the class {@link QueriedJots}
 */
public class QueriedJotsTest {

    /**
     * Check created time exist
     */
    @Test
    public void createdTimeExist() throws Exception {
        Source<Connection> db = new JotsDb(new MemoryH2Conn());
        new NewJot(db, "content").value();
        List<Jot> jots = new QueriedJots(new AllJots(db)).value();
        Assert.assertNotEquals(
                0,
                jots.get(0).createdTime()
        );
    }
}