package com.larryhsiao.nyx.view.settings

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.larryhsiao.nyx.IPv4Mapping
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.web.WebAccessService
import com.silverhetch.aura.fingerprint.FingerprintImpl
import com.silverhetch.aura.storage.SPCeres

/**
 * Setting page of Nyx
 */
class SettingFragment : PreferenceFragmentCompat() {
    private lateinit var fingerprintPref: SwitchPreference

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.setting, rootKey)
        findPreference<SwitchPreference>(
            getString(R.string.prefKey_fingerprintAuth)
        )?.apply {
            fingerprintPref = this
            val fingerprint = FingerprintImpl(
                FingerprintManagerCompat.from(context),
                SPCeres(
                    PreferenceManager.getDefaultSharedPreferences(context)
                )
            )
            isVisible = fingerprint.isSupported()
            setOnPreferenceChangeListener { preference, obj ->
                if (obj is Boolean && obj) {
                    activity?.also {
                        BioAuth(it) { _, err ->
                            fingerprintPref.isChecked = false
                            Toast.makeText(
                                context,
                                err,
                                Toast.LENGTH_LONG
                            ).show()
                        }.fire()
                    }
                }
                true
            }
        }
        findPreference<SwitchPreference>(
            getString(R.string.prefKey_webAccess)
        )?.apply {
            updateWebAccessView()
            this.setOnPreferenceChangeListener { preference, obj ->
                activity?.also {
                    updateWebAccessView()
                    if (obj is Boolean && obj) {
                        it.startService(
                            Intent(
                                it,
                                WebAccessService::class.java
                            )
                        )
                    } else {
                        it.stopService(Intent(it, WebAccessService::class.java))
                    }
                }
                true
            }
        }
    }

    private fun updateWebAccessView() {
        findPreference<SwitchPreference>(
            getString(R.string.prefKey_webAccess)
        )?.apply {
            Handler().post {
                summary = if (isChecked) {
                    StringBuilder().apply {
                        IPv4Mapping().value().forEach {
                            appendln(
                                """${String.format(
                                    "%1\$-7s",
                                    it.key
                                )} : ${it.value.hostAddress}:${WebAccessService.PORT}"""
                            )
                        }
                    }.toString()
                } else {
                    ""
                }
            }
        }
    }
}