package com.larryhsiao.nyx.settings;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.silverhetch.clotho.Source;

/**
 * Default SharedPreference which also set up at setting page.
 */
public class DefaultPreference implements Source<SharedPreferences> {
    private final Context context;

    public DefaultPreference(Context context) {this.context = context;}

    @Override
    public SharedPreferences value() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
