package com.larryhsiao.nyx.settings;

import android.content.SharedPreferences;
import com.silverhetch.clotho.Source;

import java.util.UUID;

/**
 * Implementation of {@link NyxSettings}.
 */
public class NyxSettingsImpl implements NyxSettings {
    private final Source<SharedPreferences> pref;

    public NyxSettingsImpl(Source<SharedPreferences> pref) {
        this.pref = pref;
    }

    @Override
    public boolean bioAuthEnabled() {
        return pref.value().getBoolean("fingerprint_auth", false);
    }

    @Override
    public String encryptionKey() {
        String encryptKey = pref.value().getString("encrypt_key", "");
        if (encryptKey.isEmpty()) {
            pref.value().edit().putString(
                "encrypt_key",
                encryptKey = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 7)
            ).apply();
        }
        return encryptKey;
    }

    @Override
    public Quality imageQuality() {
        return Quality.valueOf(
            pref.value().getString(
                "image_quality",
                "GOOD"
            )
        );
    }
}
