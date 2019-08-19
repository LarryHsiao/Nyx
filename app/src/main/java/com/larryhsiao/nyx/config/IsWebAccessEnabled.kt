package com.larryhsiao.nyx.config

import android.content.Context
import android.preference.PreferenceManager
import com.larryhsiao.nyx.R
import com.silverhetch.clotho.Source

/**
 * Source to fetch the preference of web access is enabled or not.
 */
class IsWebAccessEnabled(private val context: Context) : Source<Boolean> {
    override fun value(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(
                context.getString(R.string.prefKey_webAccess),
                false
            )
    }
}