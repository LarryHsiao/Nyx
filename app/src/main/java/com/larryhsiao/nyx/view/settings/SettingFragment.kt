package com.larryhsiao.nyx.view.settings

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.placeholder.IPv4Mapping
import com.larryhsiao.nyx.web.WebAccessService
import com.silverhetch.aura.fingerprint.FingerprintImpl
import com.silverhetch.aura.permission.PermissionCallback
import com.silverhetch.aura.permission.PermissionsImpl
import com.silverhetch.aura.storage.SPCeres
import com.silverhetch.aura.view.activity.ActionBarTitle

/**
 * Setting page of Nyx
 */
class SettingFragment : PreferenceFragmentCompat(), PermissionCallback {
    private lateinit var preLoadLocation: SwitchPreference
    private lateinit var fingerprintPref: SwitchPreference
    private val locationPermission = PermissionsImpl(
        this,
        this,
        arrayOf(ACCESS_FINE_LOCATION)
    )

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
            this.setOnPreferenceChangeListener { _, obj ->
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

        findPreference<SwitchPreference>(
            getString(R.string.prefKey_preloadLocation)
        )?.let { pref ->
            preLoadLocation = pref
            if (checkSelfPermission(
                    pref.context,
                    ACCESS_FINE_LOCATION
                ) != PERMISSION_GRANTED
            ) {
                pref.isChecked = false
            }
            pref.setOnPreferenceChangeListener { _, obj ->
                if (obj is Boolean && obj) {
                    locationPermission.requestPermissions()
                }
                true
            }
        }
    }

    override fun onPermissionGranted() {
    }

    override fun onPermissionPermanentlyDecline(permission: Array<String>) {
        if (permission.contains(ACCESS_FINE_LOCATION)) {
            preLoadLocation.isChecked = false
        }
    }

    override fun showPermissionRationale(permission: Array<String>) {
        if (permission.contains(ACCESS_FINE_LOCATION)) {
            preLoadLocation.isChecked = false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermission.handleResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        ActionBarTitle(
            activity,
            getString(R.string.settings)
        ).fire()
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