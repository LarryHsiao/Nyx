package com.larryhsiao.nyx.core.jots

import com.larryhsiao.nyx.core.NyxDb
import com.larryhsiao.nyx.core.tags.NewJotTag
import com.larryhsiao.nyx.core.tags.NewTag
import com.silverhetch.clotho.Source
import com.silverhetch.clotho.source.ConstSource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestTemplate
import java.nio.file.Files
import java.sql.Connection
import java.util.*

internal class JotsByDateRangeTest {

    @Test
    fun normalCase() {
        val db: Source<Connection> = NyxDb(
            Files.createTempFile("prefix", "db").toFile()
        )
        NewJot(
            db,
            "title",
            "content",
            Calendar.getInstance(),
            ""
        ).value()
        NewJot(
            db,
            "title",
            "new_jot_",
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) },
            ""
        ).value()
        val jots = QueriedJots(JotsByDateRange(
            db,
            Calendar.getInstance(),
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) },
        )).value()
        assertEquals(2, jots.size)
    }

    @Test
    fun singleDate(){
        val db: Source<Connection> = NyxDb(
            Files.createTempFile("prefix", "db").toFile()
        )
        NewJot(
            db,
            "title",
            "content",
            Calendar.getInstance(),
            ""
        ).value()
        NewJot(
            db,
            "title",
            "new_jot_",
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) },
            ""
        ).value()
        val jots = QueriedJots(JotsByDateRange(
            db,
            Calendar.getInstance(),
            Calendar.getInstance(),
        )).value()
        assertEquals(1, jots.size)
        assertEquals("content", jots[0].content())
    }
}