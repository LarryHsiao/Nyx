package com.larryhsiao.nyx;

import android.app.Application;
import com.larryhsiao.nyx.attachments.AttachmentDb;
import com.larryhsiao.nyx.jots.JotsDb;
import com.larryhsiao.nyx.tags.TagDb;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.SingleConn;
import com.silverhetch.clotho.database.h2.EmbedH2Conn;
import com.silverhetch.clotho.source.ConstSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.android.ContextHolder;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * Application of Jot.
 */
public class JotApplication extends Application {
    public long lastAuthed = 0L;
    public Source<Connection> db;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        File dbFile = new File(getFilesDir(), "jot");
        db = new SingleConn(new AttachmentDb(
            new TagDb(
                new JotsDb(
                    new EmbedH2Conn(
                        new ConstSource<>(dbFile))))));
        try {
            Connection conn = db.value();
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet rs = metadata.getColumns(null, null, "jots", "mood");
            if (!rs.next()) { // mood field not exist
                conn.createStatement().executeUpdate(
                    "ALTER TABLE jots\n" +
                        "ADD COLUMN mood varchar not null default '';"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ContextHolder.setContext(this);
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
    }
}
