package com.larryhsiao.nyx;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.larryhsiao.nyx.android.jot.JotListFragment;
import com.larryhsiao.nyx.android.jot.NewJotFragment;
import com.silverhetch.aura.AuraActivity;

/**
 * Entry Activity of Nyx.
 */
public class MainActivity extends AuraActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupPageControl(R.id.main_root);
        rootPage(new JotListFragment());
    }
}
