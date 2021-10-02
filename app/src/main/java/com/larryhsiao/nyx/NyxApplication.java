package com.larryhsiao.nyx;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.larryhsiao.aura.storage.SPCeres;
import com.larryhsiao.clotho.database.SingleConn;
import com.larryhsiao.clotho.source.SingleRefSource;
import com.larryhsiao.clotho.storage.Ceres;
import com.larryhsiao.nyx.attachment.NyxFilesImpl;
import com.larryhsiao.nyx.core.LocalNyx;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.NyxDb;
import com.larryhsiao.nyx.core.sync.SyncImpl;
import com.larryhsiao.nyx.core.sync.Syncs;
import com.larryhsiao.nyx.syncs.DropboxLogin;
import com.larryhsiao.nyx.syncs.StoredTokenSrc;
import org.flywaydb.core.api.android.ContextHolder;

import java.io.File;

public class NyxApplication extends Application {
    public static final String CHANNEL_ID_SYNC = "SYNC";

    public static final int NOTIFICATION_ID_SYNC = 1000;

    public static final int SERVICE_ID_SYNC = 1000;
    public static final String FILE_PROVIDER_AUTHORITY = "content://com.larryhsiao.nyx.fileprovider";
    public static final String URI_FILE_PROVIDER = FILE_PROVIDER_AUTHORITY + "/attachments/";
    public static final String URI_FILE_TEMP_PROVIDER = FILE_PROVIDER_AUTHORITY + "/attachments_temp/";
    private Nyx nyx;
    private Ceres storage;
    private Syncs syncs;

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
        storage = new SPCeres(
            getSharedPreferences(
                "settings",
                Context.MODE_PRIVATE
            )
        );
        syncs = new SyncImpl(
            nyx,
            new DropboxLogin(
                this,
                BuildConfig.DROPBOX_API_KEY
            ),
            getStorage(),
            new SingleRefSource<>(
                new StoredTokenSrc(getStorage())
            )
        );
    }

    public Nyx nyx() {
        return nyx;
    }

    public Ceres getStorage() {
        return storage;
    }

    public Syncs getSyncs(){
        return syncs;
    }
}
