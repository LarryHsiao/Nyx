package com.larryhsiao.nyx.settings;

import android.os.Bundle;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.preference.PreferenceFragmentCompat;
import com.larryhsiao.nyx.R;
import com.silverhetch.aura.fingerprint.FingerprintImpl;
import com.silverhetch.clotho.storage.MemoryCeres;

/**
 * Setting page.
 */
public class SettingFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        FingerprintImpl fingerprint = new FingerprintImpl(
            FingerprintManagerCompat.from(requireContext()),
            new MemoryCeres()
        );
        fingerprint.enable(true);
        findPreference("fingerprint_auth").setVisible(fingerprint.isEnabled());
    }
}
