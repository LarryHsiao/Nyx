package com.larryhsiao.nyx.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.larryhsiao.nyx.NyxFragment;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.core.sync.SyncImpl;
import com.larryhsiao.nyx.core.sync.Syncs;
import com.larryhsiao.nyx.syncs.DropboxLogin;

public class SettingFragment extends NyxFragment {
    private Syncs syncs;

    @Override
    public void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);
        syncs = new SyncImpl(
            getApp().nyx(),
            new DropboxLogin(requireContext(), "uxql3ekxg82j8aq")
        );
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView loginIndicator = view.findViewById(R.id.setting_loginIndicator);
        if (syncs.loggedInDest().contains(Syncs.Dest.DROPBOX)) {
            loginIndicator.setText(R.string.logout);
        }else{
            loginIndicator.setText(R.string.login);
            loginIndicator.setOnClickListener(v -> {
                syncs.login(
                    Syncs.Dest.DROPBOX,
                    () -> {
                    }
                );
            });
        }
    }
}
