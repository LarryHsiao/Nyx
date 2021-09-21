package com.larryhsiao.nyx;

import android.app.Application;
import android.os.Build;
import com.larryhsiao.clotho.database.SingleConn;
import com.larryhsiao.nyx.attachment.NyxFilesImpl;
import com.larryhsiao.nyx.core.LocalNyx;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.NyxDb;
import com.larryhsiao.nyx.core.sync.SyncImpl;
import com.larryhsiao.nyx.core.sync.Syncs;
import com.larryhsiao.nyx.syncs.DropboxAuthCodeFlow;
import org.flywaydb.core.api.android.ContextHolder;

import java.io.File;

public class NyxApplication extends Application {
    public static final String FILE_PROVIDER_AUTHORITY = "content://com.larryhsiao.nyx.fileprovider";
    public static final String URI_FILE_PROVIDER = FILE_PROVIDER_AUTHORITY + "/attachments/";
    public static final String URI_FILE_TEMP_PROVIDER = FILE_PROVIDER_AUTHORITY + "/attachments_temp/";
    private Nyx nyx;
    private Syncs sync;

    @Override
    public void onCreate() {
        super.onCreate();
        ContextHolder.setContext(this);
        if ("robolectric".equals(Build.FINGERPRINT)) {
            return;
        }
        nyx = new LocalNyx(
            new SingleConn(
                new NyxDb(
                    new File(
                        getFilesDir(),
                        "jot"
                    ),
                    false
                )
            ),
            new NyxFilesImpl(this)
        );
        sync = new SyncImpl(
            nyx,
            new DropboxAuthCodeFlow(this)
        );
    }

    public Nyx nyx(){
        return nyx;
    }
}
