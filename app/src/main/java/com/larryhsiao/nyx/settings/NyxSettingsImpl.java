package com.larryhsiao.nyx.settings;

import android.content.SharedPreferences;
import com.larryhsiao.clotho.Source;

/**
 * Implementation of {@link NyxSettings}.
 */
public class NyxSettingsImpl implements NyxSettings {
    private final Source<SharedPreferences> pref;

    public NyxSettingsImpl(Source<SharedPreferences> pref) {
        this.pref = pref;
    }

    @Override
    public Quality imageQuality() {
        return Quality.valueOf(
            pref.value().getString("image_quality", "GOOD")
        );
    }
}
