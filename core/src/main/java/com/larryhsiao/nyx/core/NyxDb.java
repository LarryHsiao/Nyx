package com.larryhsiao.nyx.core;

import com.larryhsiao.nyx.core.attachments.AttachmentDb;
import com.larryhsiao.nyx.core.jots.JotsDb;
import com.larryhsiao.nyx.core.tags.TagDb;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.h2.EmbedH2Conn;
import com.silverhetch.clotho.source.ConstSource;
import org.flywaydb.core.Flyway;

import java.io.File;
import java.sql.Connection;

/**
 * Source to build db connection for Nyx.
 */
public class NyxDb implements Source<Connection> {
    private final File dbFile;

    public NyxDb(File dbFile) {
        this.dbFile = dbFile;
    }

    @Override
    public Connection value() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        final Connection conn = new AttachmentDb(
            new TagDb(
                new JotsDb(
                    new EmbedH2Conn(
                        new ConstSource<>(dbFile)
                    )
                )
            )
        ).value();
        Flyway flyway = Flyway.configure()
            .baselineOnMigrate(true)
            .baselineVersion("2")
            .dataSource("jdbc:h2:" +
                    dbFile.getAbsolutePath() +
                    ";FILE_LOCK=FS" +
                    ";PAGE_SIZE=1024" +
                    ";CACHE_SIZE=8192",
                null,
                null
            ).load();
        flyway.migrate();
        return conn;
    }
}
