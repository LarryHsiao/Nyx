package com.larryhsiao.nyx.config

import android.content.Context
import android.preference.PreferenceManager
import com.larryhsiao.nyx.R
import java.io.File

/**
 * Implementation of [Config], the default behavior of Nyx.
 */
class ConfigImpl(private val context: Context) :
    Config {
    companion object {
        private const val PATH_MEDIA = "medias/"
    }

    override fun mediaRoot(): File {
        return File(context.filesDir,
            PATH_MEDIA
        ).also {
            if (it.exists().not()) {
                it.mkdirs()
            }
        }
    }

    override fun bioAuthEnabled(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(
                context.getString(R.string.prefKey_fingerprintAuth),
                false
            )
    }
}