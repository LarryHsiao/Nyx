package com.larryhsiao.nyx.core.jots

import com.larryhsiao.clotho.Source
import com.larryhsiao.clotho.database.h2.MemoryH2Conn
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Connection

/**
 * Unit-test for the class [QueriedJots]
 */
class QueriedJotsTest {
    /**
     * Check created time exist
     */
    @Test
    @Throws(Exception::class)
    fun createdTimeExist() {
        val db: Source<Connection> = JotsDb(MemoryH2Conn())
        NewJot(db, "title", "content").value()
        val jots = QueriedJots(AllJots(db)).value()
        Assertions.assertNotEquals(
            0,
            jots[0].createdTime()
        )
    }
}