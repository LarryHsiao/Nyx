package com.larryhsiao.nyx;

import android.app.Application;
import android.os.Build;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
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
    public static final String URI_FILE_PROVIDER = "content://com.larryhsiao.nyx.fileprovider/attachments/";
    public static final String URI_FILE_TEMP_PROVIDER = "content://com.larryhsiao.nyx.fileprovider/attachments_temp/";
    public long lastAuthed = 0L;
    public Source<Connection> db;
    public FirebaseRemoteConfig remoteConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        ContextHolder.setContext(this);
        File dbFile = new File(getFilesDir(), "jot");
        db = new SingleConn(new NyxDb(dbFile));
        if ("robolectric".equals(Build.FINGERPRINT)) {
            return;
        }
        remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        remoteConfig.setConfigSettingsAsync(
            new FirebaseRemoteConfigSettings.Builder()
                .setFetchTimeoutInSeconds(60)
                .build()
        );
        remoteConfig.fetchAndActivate();
    }
}
