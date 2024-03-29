package com.larryhsiao.nyx.core

import com.larryhsiao.nyx.core.attachments.AttachmentDb
import com.larryhsiao.nyx.core.jots.JotsDb
import com.larryhsiao.nyx.core.metadata.MetadataDb
import com.larryhsiao.nyx.core.tags.TagDb
import com.larryhsiao.clotho.Source
import com.larryhsiao.clotho.database.h2.EmbedH2Conn
import com.larryhsiao.clotho.database.h2.MemoryH2Conn
import com.larryhsiao.clotho.source.ConstSource
import org.flywaydb.core.Flyway
import java.io.File
import java.sql.Connection

/**
 * Source to build db connection for Nyx.
 */
class NyxDb(private val dbFile: File, private val memoryMode: Boolean = false) :
    Source<Connection> {
    override fun value(): Connection {
        try {
            Class.forName("org.h2.Driver")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        val conn = MetadataDb(
            AttachmentDb(
                TagDb(
                    JotsDb(
                        if (memoryMode) {
                            MemoryH2Conn()
                        } else {
                            EmbedH2Conn(ConstSource(dbFile))
                        }
                    )
                )
            )
        ).value()
        if (memoryMode) {
            return conn
        } else {
            val flyway = Flyway.configure()
                .baselineOnMigrate(true)
                .baselineVersion("5")
                .dataSource(
                    "jdbc:h2:" +
                        dbFile.absolutePath +
                        ";FILE_LOCK=FS" +
                        ";PAGE_SIZE=1024" +
                        ";CACHE_SIZE=8192",
                    null,
                    null
                ).load()
            flyway.migrate()
            return conn
        }
    }
}