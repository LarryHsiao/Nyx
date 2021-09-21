package com.larryhsiao.nyx.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.larryhsiao.nyx.NyxFragment;
import com.larryhsiao.nyx.R;

public class SettingFragment extends NyxFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
            R.layout.fragment_setting,
            container,
            false
        );
    }
}
