package com.larryhsiao.nyx.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.base.JotFragment;

import java.util.Arrays;

import static android.app.Activity.RESULT_OK;

/**
 * Account page
 */
public class AccountFragment extends JotFragment {
    private static final int REQUEST_CODE_LOG_IN = 1000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateView(view);
    }

    private void updateView(View view) {
        TextView info = view.findViewById(R.id.account_info);
        Button loginLogoutBtn = view.findViewById(R.id.account_login_logout);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean isLoggedIn = user != null;
        if (isLoggedIn) {
            info.setText(user.getEmail() + " " + user.getUid());
            loginLogoutBtn.setText(R.string.logout);
            loginLogoutBtn.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                updateView(view);
            });
            new SyncJots(user.getUid(), db).fire();
            new SyncTags(user.getUid(), db).fire();
            new SyncTagJot(user.getUid(), db).fire();
            new SyncAttachments(user.getUid(), db).fire();
        } else {
            info.setText("");
            loginLogoutBtn.setText(R.string.login);
            loginLogoutBtn.setOnClickListener(v -> {
                startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build()
                    )).build(), REQUEST_CODE_LOG_IN
                );
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOG_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                ((TextView) getView().findViewById(R.id.account_info)).setText(
                    user.getEmail() + " " + user.getUid()
                );
                updateView(getView());
            } else {
                Toast.makeText(getContext(),
                    "error " + response,
                    Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
