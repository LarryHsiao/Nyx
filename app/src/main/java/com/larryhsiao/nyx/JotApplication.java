package com.larryhsiao.nyx;

import android.app.Application;
import com.larryhsiao.nyx.attachments.AttachmentDb;
import com.larryhsiao.nyx.jots.JotsDb;
import com.larryhsiao.nyx.tags.TagDb;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.SingleConn;
import com.silverhetch.clotho.database.h2.EmbedH2Conn;
import com.silverhetch.clotho.source.ConstSource;

import java.io.File;
import java.sql.Connection;

/**
 * Application of Jot.
 */
public class JotApplication extends Application {
    public Source<Connection> db;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        db = new SingleConn(new AttachmentDb(
            new TagDb(
                new JotsDb(
                    new EmbedH2Conn(
                        new ConstSource<>(new File(getFilesDir(), "jot")))))));
    }
}
