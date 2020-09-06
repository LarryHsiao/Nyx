package com.larryhsiao.nyx.old.analytics;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.old.base.JotFragment;

/**
 * Analytics page of Jots.
 */
public class AnalyticsFragment extends JotFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.analytics));
    }
}
