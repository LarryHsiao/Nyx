package com.larryhsiao.nyx.core.jots

import com.larryhsiao.clotho.Source
import com.larryhsiao.clotho.database.h2.MemoryH2Conn
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.SQLException

/**
 * Unit-test for the class [NewJot]
 */
class NewJotTest {
    /**
     * Check insert success.
     */
    @Test
    @Throws(SQLException::class)
    fun simple() {
        val db: Source<Connection> = JotsDb(MemoryH2Conn())
        NewJot(db, "title", "content").value()
        AllJots(db).value().use { res ->
            var count = 0
            while (res.next()) {
                count++
            }
            Assertions.assertEquals(1, count)
        }
    }
}