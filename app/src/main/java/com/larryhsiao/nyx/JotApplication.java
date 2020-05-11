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
    public static final String SKU_DRIVE_BACKUP = "cloud_backup";
    public static final String SKU_PREMIUM = "premium";
    public static final String URI_FILE_PROVIDER = "content://com.larryhsiao.nyx.fileprovider/attachments/";
    public static final String URI_FILE_TEMP_PROVIDER = "content://com.larryhsiao.nyx.fileprovider/attachments_temp/";
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
