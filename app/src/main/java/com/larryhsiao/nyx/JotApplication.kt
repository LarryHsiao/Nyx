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
    companion object {
        @Deprecated("Remove it after 4.0 conpleted")
        @JvmField val FILE_PROVIDER_AUTHORITY: String?

        @Deprecated("Remove it after 4.0 conpleted")
        @JvmField val URI_FILE_PROVIDER: String?

        @Deprecated("Remove it after 4.0 conpleted")
        @JvmField val URI_FILE_TEMP_PROVIDER: String?

        init {
            FILE_PROVIDER_AUTHORITY = "com.larryhsiao.nyx.fileprovider"
            URI_FILE_PROVIDER = "content://$FILE_PROVIDER_AUTHORITY/attachments/"
            URI_FILE_TEMP_PROVIDER = "content://$FILE_PROVIDER_AUTHORITY/attachments_temp/"
        }
    }

    @Deprecated("Remove it after 4.0 conpleted")
    @JvmField var lastAuthed = 0L

    @Deprecated("Remove it after 4.0 conpleted")
    @JvmField var dbSrc: Source<Connection>? = null

    @Deprecated("Remove it after 4.0 conpleted")
    @JvmField var remoteConfig: FirebaseRemoteConfig? = null

    val db: Source<Connection> by lazy { SingleConn(NyxDb(File(filesDir, "jot"))) }
    val config: Config by lazy {
        RemoteConfig(
            FirebaseRemoteConfig.getInstance().apply {
                setDefaultsAsync(R.xml.remote_config_defaults)
                setConfigSettingsAsync(
                    FirebaseRemoteConfigSettings.Builder()
                        .setFetchTimeoutInSeconds(60)
                        .build()
                )
                fetchAndActivate()
            }
        )
    }

    override fun onCreate() {
        super.onCreate()
        ContextHolder.setContext(this)
        val dbFile = File(filesDir, "jot")
        dbSrc = SingleConn(NyxDb(dbFile))
        if ("robolectric" == Build.FINGERPRINT) {
            return
        }
    }
}