package com.larryhsiao.nyx.core.jots;

import com.larryhsiao.nyx.core.NyxDb;
import com.larryhsiao.nyx.core.tags.NewJotTag;
import com.larryhsiao.nyx.core.tags.NewTag;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.source.ConstSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.List;

/**
 * Unit-test for the class {@link JotsByKeyword}
 */
class JotsByKeywordTest {

    /**
     * Check the output of searching by tag name
     */
    @Test
    void searchByTagName() throws IOException {
        Source<Connection> db = new NyxDb(
            Files.createTempFile("prefix", "db").toFile()
        );
        new NewJot(db, "content", new double[]{0.0, 0.0}, "").value();
        new NewJot(db, "new_jot_", new double[]{0.0, 0.0}, "").value();
        new NewJotTag(
            db,
            new ConstSource<>(2L),
            new ConstSource<>(new NewTag(db, "Tag").value().id())
        ).fire();
        List<Jot> jots = new QueriedJots(new JotsByKeyword(db, "Tag")).value();
        Assertions.assertEquals(1, jots.size());
        Assertions.assertEquals("new_jot_", jots.get(0).content());
    }

    /**
     * Check the output of searching by jot content.
     */
    @Test
    void searchByContent() throws IOException {
        Source<Connection> db = new NyxDb(
            Files.createTempFile("prefix", "db").toFile()
        );
        new NewJot(db, "content", new double[]{0.0, 0.0}, "").value();
        new NewJot(db, "new_jot_", new double[]{0.0, 0.0}, "").value();
        new NewJotTag(
            db,
            new ConstSource<>(2L),
            new ConstSource<>(new NewTag(db, "Tag").value().id())
        ).fire();
        List<Jot> jots = new QueriedJots(new JotsByKeyword(db, "content")).value();
        Assertions.assertEquals(1, jots.size());
        Assertions.assertEquals("content", jots.get(0).content());
    }

    /**
     * Check the output of searching with multiple match in content and tags.
     * The result should not have duplicate items.
     * <p>
     * If condition match to a Jot and its Tag, the search result should only returns one row.
     */
    @Test
    void searchMultipleMatch() throws IOException {
        Source<Connection> db = new NyxDb(
            Files.createTempFile("prefix", "db").toFile()
        );
        new NewJot(db, "Content", new double[]{0.0, 0.0}, "").value();
        new NewJot(db, "Tag", new double[]{0.0, 0.0}, "").value();
        new NewJotTag(
            db,
            new ConstSource<>(2L),
            new ConstSource<>(new NewTag(db, "Tag").value().id())
        ).fire();
        List<Jot> jots = new QueriedJots(new JotsByKeyword(db, "Tag")).value();
        Assertions.assertEquals(1, jots.size());
        Assertions.assertEquals("Tag", jots.get(0).content());
    }
}