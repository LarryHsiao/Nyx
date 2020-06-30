package com.larryhsiao.nyx;

import android.app.Application;
import android.os.Build;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.larryhsiao.nyx.core.NyxDb;
import com.larryhsiao.nyx.settings.DefaultPreference;
import com.larryhsiao.nyx.settings.NyxSettingsImpl;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.SingleConn;
import com.silverhetch.clotho.source.SingleRefSource;
import org.flywaydb.core.api.android.ContextHolder;

import java.io.File;
import java.sql.Connection;


/**
 * Application of Jot.
 */
public class JotApplication extends Application {
    public static final String FILE_PROVIDER_AUTHORITY;
    public static final String URI_FILE_PROVIDER;
    public static final String URI_FILE_TEMP_PROVIDER;
    public long lastAuthed = 0L;
    public Source<Connection> db;
    public FirebaseRemoteConfig remoteConfig;

    static {
        FILE_PROVIDER_AUTHORITY = "com.larryhsiao.nyx.fileprovider";
        URI_FILE_PROVIDER = "content://" + FILE_PROVIDER_AUTHORITY + "/attachments/";
        URI_FILE_TEMP_PROVIDER = "content://" + FILE_PROVIDER_AUTHORITY + "/attachments_temp/";
    }

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

        /**
         * Initial encryption key
         */
        new NyxSettingsImpl(
            new SingleRefSource<>(
                new DefaultPreference(this)
            )
        ).encryptionKey();
    }
}
