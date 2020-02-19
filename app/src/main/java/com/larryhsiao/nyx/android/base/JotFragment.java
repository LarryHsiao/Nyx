package com.larryhsiao.nyx.android.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.larryhsiao.nyx.JotApplication;
import com.silverhetch.aura.AuraFragment;
import com.silverhetch.clotho.Source;

import java.sql.Connection;

/**
 * Base fragment for Jot
 */
public class JotFragment extends AuraFragment {
    protected Source<Connection> db;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = ((JotApplication) getContext().getApplicationContext()).db;
    }
}
