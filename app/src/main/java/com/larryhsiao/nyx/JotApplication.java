package com.larryhsiao.nyx;

import android.app.Application;
import com.larryhsiao.nyx.core.NyxDb;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.SingleConn;
import org.flywaydb.core.api.android.ContextHolder;

import java.io.File;
import java.sql.Connection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Application of Jot.
 */
public class JotApplication extends Application {
    public final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    public final ExecutorService executor = new ThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors() * 2,
        1,
        TimeUnit.SECONDS,
        queue,
        new JotThreadFactory()
    );
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
