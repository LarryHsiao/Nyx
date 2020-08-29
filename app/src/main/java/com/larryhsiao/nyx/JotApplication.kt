package com.larryhsiao.nyx

import android.app.Application
import android.os.Build
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.larryhsiao.nyx.core.NyxDb
import com.silverhetch.clotho.Source
import com.silverhetch.clotho.database.SingleConn
import org.flywaydb.core.api.android.ContextHolder
import java.io.File
import java.sql.Connection

/**
 * Application of Jot.
 */
class JotApplication : Application() {
    @JvmField var lastAuthed = 0L
    @JvmField var db: Source<Connection>? = null
    @JvmField var remoteConfig: FirebaseRemoteConfig? = null

    companion object {
        @JvmField val FILE_PROVIDER_AUTHORITY: String?
        @JvmField val URI_FILE_PROVIDER: String?
        @JvmField val URI_FILE_TEMP_PROVIDER: String?

        init {
            FILE_PROVIDER_AUTHORITY = "com.larryhsiao.nyx.fileprovider"
            URI_FILE_PROVIDER = "content://$FILE_PROVIDER_AUTHORITY/attachments/"
            URI_FILE_TEMP_PROVIDER = "content://$FILE_PROVIDER_AUTHORITY/attachments_temp/"
        }
    }

    override fun onCreate() {
        super.onCreate()
        ContextHolder.setContext(this)
        val dbFile = File(filesDir, "jot")
        db = SingleConn(NyxDb(dbFile))
        if ("robolectric" == Build.FINGERPRINT) {
            return
        }
        remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig!!.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig!!.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setFetchTimeoutInSeconds(60)
                .build()
        )
        remoteConfig!!.fetchAndActivate()
    }
}