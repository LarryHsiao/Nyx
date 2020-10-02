package com.larryhsiao.nyx.old.base;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import com.larryhsiao.nyx.JotApplication;
import com.larryhsiao.nyx.old.settings.DefaultPreference;
import com.larryhsiao.nyx.old.settings.NyxSettings;
import com.larryhsiao.nyx.old.settings.NyxSettingsImpl;
import com.silverhetch.aura.AuraActivity;
import com.silverhetch.aura.fingerprint.Fingerprint;
import com.silverhetch.aura.fingerprint.FingerprintImpl;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.source.SingleRefSource;
import com.silverhetch.clotho.storage.MemoryCeres;

import java.sql.Connection;

/**
 * Activity for Jot.
 */
public abstract class JotActivity extends AuraActivity {
    protected Source<Connection> db;
    protected NyxSettings settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JotApplication app = ((JotApplication) getApplicationContext());
        db = app.getDb();
        settings = new NyxSettingsImpl(new SingleRefSource<>(new DefaultPreference(this)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Fingerprint fingerprint = new FingerprintImpl(
            FingerprintManagerCompat.from(this), new MemoryCeres()
        );
        fingerprint.enable(settings.bioAuthEnabled());
        JotApplication app = (JotApplication) getApplicationContext();
        if (fingerprint.isEnabled() && 300000 < System.currentTimeMillis() - app.lastAuthed) {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
        }
    }
}
