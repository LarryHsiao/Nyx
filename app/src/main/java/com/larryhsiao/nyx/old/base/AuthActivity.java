package com.larryhsiao.nyx.old.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.larryhsiao.nyx.NyxApplication;
import com.larryhsiao.aura.fingerprint.BioAuth;
import kotlin.Unit;

/**
 * Activity to auth by fingerprint
 */
public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NyxApplication app = (NyxApplication) getApplicationContext();
        new BioAuth(this,
            () -> {
                app.lastAuthed = System.currentTimeMillis();
                finish();
                return Unit.INSTANCE;
            }, (integer, s) -> {
            finishAffinity();
            return Unit.INSTANCE;
        }
        ).fire();
    }

    @Override
    public void onBackPressed() {
        // No back button
    }
}
