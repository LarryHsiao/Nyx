package com.larryhsiao.nyx.core.jots

import com.larryhsiao.nyx.core.NyxDb
import com.larryhsiao.nyx.core.tags.NewJotTag
import com.larryhsiao.nyx.core.tags.NewTag
import com.larryhsiao.clotho.Source
import com.larryhsiao.clotho.source.ConstSource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.sql.Connection

/**
 * Unit-test for the class [JotsByKeyword]
 */
internal class JotsByKeywordTest {
    /**
     * Check the output of searching by tag name
     */
    @Test
    @Throws(IOException::class)
    fun searchByTagName() {
        val db: Source<Connection> = NyxDb(
            Files.createTempFile("prefix", "db").toFile()
        )
        NewJot(db, "title", "content", doubleArrayOf(0.0, 0.0), "").value()
        NewJot(db, "title", "new_jot_", doubleArrayOf(0.0, 0.0), "").value()
        NewJotTag(
            db,
            ConstSource(2L),
            ConstSource(NewTag(db, "Tag").value().id())
        ).fire()
        val jots = QueriedJots(JotsByKeyword(db, "Tag")).value()
        Assertions.assertEquals(1, jots.size)
        Assertions.assertEquals("new_jot_", jots[0].content())
    }

    /**
     * Check the output of searching by jot content.
     */
    @Test
    @Throws(IOException::class)
    fun searchByContent() {
        val db: Source<Connection> = NyxDb(
            Files.createTempFile("prefix", "db").toFile()
        )
        NewJot(db, "title", "content", doubleArrayOf(0.0, 0.0), "").value()
        NewJot(db, "title", "new_jot_", doubleArrayOf(0.0, 0.0), "").value()
        NewJotTag(
            db,
            ConstSource(2L),
            ConstSource(NewTag(db, "Tag").value().id())
        ).fire()
        val jots = QueriedJots(JotsByKeyword(db, "content")).value()
        Assertions.assertEquals(1, jots.size)
        Assertions.assertEquals("content", jots[0].content())
    }

    /**
     * Check the output of searching with multiple match in content and tags.
     * The result should not have duplicate items.
     *
     *
     * If condition match to a Jot and its Tag, the search result should only returns one row.
     */
    @Test
    @Throws(IOException::class)
    fun searchMultipleMatch() {
        val db: Source<Connection> = NyxDb(
            Files.createTempFile("prefix", "db").toFile()
        )
        NewJot(db, "title", "Content", doubleArrayOf(0.0, 0.0), "").value()
        NewJot(db, "title", "Tag", doubleArrayOf(0.0, 0.0), "").value()
        NewJotTag(
            db,
            ConstSource(2L),
            ConstSource(NewTag(db, "Tag").value().id())
        ).fire()
        val jots = QueriedJots(JotsByKeyword(db, "Tag")).value()
        Assertions.assertEquals(1, jots.size)
        Assertions.assertEquals("Tag", jots[0].content())
    }
}