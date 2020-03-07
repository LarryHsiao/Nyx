package com.larryhsiao.nyx;

import android.app.Application;
import com.larryhsiao.nyx.core.NyxDb;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.SingleConn;
import org.flywaydb.core.api.android.ContextHolder;

import java.io.File;
import java.sql.Connection;

/**
 * Application of Jot.
 */
public class JotApplication extends Application {
    public long lastAuthed = 0L;
    public Source<Connection> db;

    @Override
    public void onCreate() {
        super.onCreate();
        ContextHolder.setContext(this);
        File dbFile = new File(getFilesDir(), "jot");
        db = new SingleConn(new NyxDb(dbFile));
    }
}
