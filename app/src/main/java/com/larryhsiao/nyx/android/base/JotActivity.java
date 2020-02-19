package com.larryhsiao.nyx.android.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.larryhsiao.nyx.JotApplication;
import com.silverhetch.aura.AuraActivity;
import com.silverhetch.clotho.Source;

import java.sql.Connection;

/**
 * Activity for Jot.
 */
public abstract class JotActivity extends AuraActivity {
    private Source<Connection> db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = ((JotApplication) getApplicationContext()).db;
    }
}
