package com.larryhsiao.nyx.core.tags;

import com.larryhsiao.nyx.core.jots.JotsDb;
import com.larryhsiao.nyx.core.jots.NewJot;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.h2.MemoryH2Conn;
import com.silverhetch.clotho.source.ConstSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

/**
 * Unit-test for the class {@link CombineTags}.
 */
class CombineTagsTest {

    /**
     * Check the output.
     */
    @Test
    void simple() {
        Source<Connection> db = new TagDb(new JotsDb(new MemoryH2Conn()));
        new NewJot(db, "", "content").value();
        new NewJot(db, "", "content2").value();
        new NewTag(db, "tag1").value();
        new NewTag(db, "tag2").value();
        new NewJotTag(db, new ConstSource<>(1L), new ConstSource<>(1L)).fire();
        new NewJotTag(db, new ConstSource<>(2L), new ConstSource<>(2L)).fire();
        new CombineTags(db, 1, 2).fire();
        Assertions.assertEquals(1, new QueriedTags(new AllTags(db)).value().size());
        Assertions.assertEquals(2,
            new QueriedJots(new JotsByTagId(
                db,
                new ConstSource<>(1L)
            )).value().size()
        );
    }
}