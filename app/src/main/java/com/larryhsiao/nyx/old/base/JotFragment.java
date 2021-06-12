package com.larryhsiao.nyx.old.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.larryhsiao.nyx.NyxApplication;
import com.larryhsiao.aura.AuraFragment;
import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.Nyx;

import java.sql.Connection;

/**
 * Base fragment for Jot
 */
public abstract class JotFragment extends AuraFragment {
    protected Nyx nyx;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nyx = ((NyxApplication) getContext().getApplicationContext()).nyx();
    }
}
