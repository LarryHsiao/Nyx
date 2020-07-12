package com.larryhsiao.nyx.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.sync.SyncService;
import com.silverhetch.aura.fingerprint.FingerprintImpl;
import com.silverhetch.clotho.storage.MemoryCeres;

/**
 * Setting page.
 */
public class SettingFragment extends PreferenceFragmentCompat
    implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preference syncNow;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        FingerprintImpl fingerprint = new FingerprintImpl(
            FingerprintManagerCompat.from(requireContext()),
            new MemoryCeres()
        );
        fingerprint.enable(true);
        findPreference("fingerprint_auth").setVisible(fingerprint.isEnabled());
        final Preference encryptKey = findPreference("encrypt_key");
        encryptKey.setOnPreferenceChangeListener((preference, newValue) -> {
            SyncService.enqueue(requireContext());
            return true;
        });

        syncNow = findPreference("sync_now");
        syncNow.setSummary(getString(
            R.string.last_synced___,
            syncNow.getSharedPreferences().getString(
                "sync_now",
                getString(R.string.none)
            )
        ));
        syncNow.setOnPreferenceClickListener(preference -> {
            SyncService.enqueue(requireContext());
            return true;
        });
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "sync_now":
                syncNow.setSummary(getString(
                    R.string.last_synced___,
                    syncNow.getSharedPreferences().getString(
                        "sync_now",
                        getString(R.string.none)
                    )
                ));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(this);
    }
}
