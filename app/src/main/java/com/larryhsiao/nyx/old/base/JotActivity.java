package com.larryhsiao.nyx.old.base;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import com.larryhsiao.nyx.NyxApplication;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.old.settings.DefaultPreference;
import com.larryhsiao.nyx.old.settings.NyxSettings;
import com.larryhsiao.nyx.old.settings.NyxSettingsImpl;
import com.larryhsiao.aura.AuraActivity;
import com.larryhsiao.aura.fingerprint.Fingerprint;
import com.larryhsiao.aura.fingerprint.FingerprintImpl;
import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.source.SingleRefSource;
import com.larryhsiao.clotho.storage.MemoryCeres;

import java.sql.Connection;

/**
 * Activity for Jot.
 */
public abstract class JotActivity extends AuraActivity {
    protected Nyx nyx;
    protected NyxSettings settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NyxApplication app = ((NyxApplication) getApplicationContext());
        nyx = app.nyx();
        settings = new NyxSettingsImpl(new SingleRefSource<>(new DefaultPreference(this)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Fingerprint fingerprint = new FingerprintImpl(
            FingerprintManagerCompat.from(this), new MemoryCeres()
        );
        fingerprint.enable(settings.bioAuthEnabled());
        NyxApplication app = (NyxApplication) getApplicationContext();
        if (fingerprint.isEnabled() && 300000 < System.currentTimeMillis() - app.lastAuthed) {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
        }
    }
}
