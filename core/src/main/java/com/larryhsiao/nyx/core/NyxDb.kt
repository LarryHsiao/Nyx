package com.larryhsiao.nyx.core

import com.larryhsiao.nyx.core.attachments.AttachmentDb
import com.larryhsiao.nyx.core.jots.JotsDb
import com.larryhsiao.nyx.core.metadata.MetadataDb
import com.larryhsiao.nyx.core.tags.TagDb
import com.silverhetch.clotho.Source
import com.silverhetch.clotho.database.h2.EmbedH2Conn
import com.silverhetch.clotho.source.ConstSource
import org.flywaydb.core.Flyway
import java.io.File
import java.sql.Connection

/**
 * Source to build db connection for Nyx.
 */
class NyxDb(private val dbFile: File) : Source<Connection> {
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
                        EmbedH2Conn(
                            ConstSource(dbFile)
                        )
                    )
                )
            )
        ).value()
        val flyway = Flyway.configure()
            .baselineOnMigrate(true)
            .baselineVersion("5")
            .dataSource("jdbc:h2:" +
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