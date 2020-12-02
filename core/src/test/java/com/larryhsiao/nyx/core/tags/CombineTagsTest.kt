package com.larryhsiao.nyx.core.tags

import com.larryhsiao.nyx.core.jots.JotsDb
import com.larryhsiao.nyx.core.jots.NewJot
import com.larryhsiao.nyx.core.jots.QueriedJots
import com.larryhsiao.clotho.Source
import com.larryhsiao.clotho.database.h2.MemoryH2Conn
import com.larryhsiao.clotho.source.ConstSource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Connection

/**
 * Unit-test for the class [CombineTags].
 */
internal class CombineTagsTest {
    /**
     * Check the output.
     */
    @Test
    fun simple() {
        val db: Source<Connection> = TagDb(JotsDb(MemoryH2Conn()))
        NewJot(db, "", "content").value()
        NewJot(db, "", "content2").value()
        NewTag(db, "tag1").value()
        NewTag(db, "tag2").value()
        NewJotTag(db, ConstSource(1L), ConstSource(1L)).fire()
        NewJotTag(db, ConstSource(2L), ConstSource(2L)).fire()
        CombineTags(db, 1, 2).fire()
        Assertions.assertEquals(1, QueriedTags(AllTags(db)).value().size)
        Assertions.assertEquals(2,
            QueriedJots(JotsByTagId(
                db,
                ConstSource(1L)
            )).value().size
        )
    }
}