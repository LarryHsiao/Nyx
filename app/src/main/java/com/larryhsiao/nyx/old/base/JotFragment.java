package com.larryhsiao.nyx.old.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.larryhsiao.nyx.NyxApplication;
import com.silverhetch.aura.AuraFragment;
import com.silverhetch.clotho.Source;

import java.sql.Connection;

/**
 * Base fragment for Jot
 */
public abstract class JotFragment extends AuraFragment {
    protected Source<Connection> db;
    protected FirebaseRemoteConfig remoteConfig;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = ((NyxApplication) getContext().getApplicationContext()).getDb();
        remoteConfig = ((NyxApplication) getContext().getApplicationContext()).remoteConfig;
    }
}
