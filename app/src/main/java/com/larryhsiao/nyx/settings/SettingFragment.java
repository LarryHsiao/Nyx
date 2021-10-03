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
import com.larryhsiao.nyx.core.sync.Jwt;
import com.larryhsiao.nyx.core.sync.Syncs;
import com.larryhsiao.nyx.core.sync.dropbox.DBAccountName;
import com.larryhsiao.nyx.syncs.SyncService;

public class SettingFragment extends NyxFragment {
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
        updateLoginUi(view);
    }

    private void updateLoginUi(View view) {
        final TextView loginIndicator = view.findViewById(R.id.setting_login);
        final TextView dropboxAccount = view.findViewById(R.id.setting_dropboxAccount);
        final View syncButton = view.findViewById(R.id.setting_sync);
        final Syncs syncs = getApp().getSyncs();
        if (syncs.loggedInDest().contains(Syncs.Dest.DROPBOX)) {
            syncButton.setVisibility(View.VISIBLE);
            syncButton.setOnClickListener(v -> new Thread(() -> {
                SyncService.enqueueWork(v.getContext());
            }).start());
            loginIndicator.setText(R.string.logout);
            loginIndicator.setOnClickListener(v -> {
                syncs.logout(Syncs.Dest.DROPBOX);
                updateLoginUi(view);
            });
            final Jwt jwt = syncs.loggedInAccount().get(Syncs.Dest.DROPBOX);
            if (jwt != null) {
                async(() -> {
                    try {
                        final String account = new DBAccountName(jwt).value();
                        view.post(() -> dropboxAccount.setText(
                            getString(R.string.Account__, account)
                        ));
                    }catch (Exception e){
                        e.printStackTrace();
                        // @todo #151 Publish the failure.
                    }
                });
            }
        } else {
            dropboxAccount.setText("");
            syncButton.setVisibility(View.GONE);
            syncButton.setOnClickListener(null);
            loginIndicator.setText(R.string.login);
            loginIndicator.setOnClickListener(v -> syncs.login(
                Syncs.Dest.DROPBOX,
                () -> updateLoginUi(view)
            ));
        }
    }
}
