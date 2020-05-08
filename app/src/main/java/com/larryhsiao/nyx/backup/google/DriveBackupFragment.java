package com.larryhsiao.nyx.backup.google;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.base.JotFragment;
import com.silverhetch.aura.view.span.ClickableStr;
import com.silverhetch.aura.view.span.ColoredStr;
import com.silverhetch.clotho.source.ConstSource;

import static android.app.Activity.RESULT_OK;
import static android.graphics.Color.BLUE;
import static androidx.swiperefreshlayout.widget.CircularProgressDrawable.LARGE;
import static com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN;
import static com.google.api.services.drive.DriveScopes.DRIVE_FILE;

/**
 * Activity to guide user to back up jots to drive.
 *
 * @todo #1 Backup service.
 * @todo #1 Notifications for the progress.
 * @todo #1 Backup/Restore selection? Or no selection.
 * @todo #1 Restrict for in-app purchased user.
 * @todo #1 Restore/Backup progress.
 */
public class DriveBackupFragment extends JotFragment {
    private static final int REQUEST_CODE_SIGN_IN = 1000;
    private GoogleSignInClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(new Scope(DRIVE_FILE))
            .build();
        client = GoogleSignIn.getClient(getContext(), signInOptions);
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle state
    ) {
        return inflater.inflate(R.layout.page_backup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(view.getContext());
        if (account == null) {
            updateAccountUI();
        } else {
            updateAccountUI(account);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode == RESULT_OK && resultData != null) {
                handleSignInResult(resultData);
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener(googleAccount -> {
                    updateAccountUI(googleAccount);
                    initDrive();
                }
            );
    }

    private void updateAccountUI() {
        getView().findViewById(R.id.backup_backupBtn).setEnabled(false);
        getView().findViewById(R.id.backup_restoreBtn).setEnabled(false);
        TextView accountInfo = getView().findViewById(R.id.backup_accountInfo);
        accountInfo.setText(
            new ClickableStr(
                new ColoredStr(
                    new ConstSource<>(getString(R.string.login)), BLUE), () -> {
                startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
            }).value());
        accountInfo.setMovementMethod(LinkMovementMethod.getInstance());

        Glide.with(this)
            .load(getResources().getDrawable(R.drawable.ic_user, null))
            .into(((ImageView) getView().findViewById(R.id.backup_userIcon)));
    }

    private void updateAccountUI(GoogleSignInAccount account) {
        getView().findViewById(R.id.backup_backupBtn).setEnabled(true);
        getView().findViewById(R.id.backup_restoreBtn).setEnabled(true);
        TextView accountText = getView().findViewById(R.id.backup_accountInfo);
        accountText.setText("");
        accountText.append(account.getDisplayName());
        accountText.append("\n");
        accountText.append(account.getEmail());
        accountText.append("\n");
        accountText.append(
            new ClickableStr(
                new ColoredStr(
                    new ConstSource<>(getString(R.string.logout)),
                    BLUE
                ),
                () -> {
                    client.signOut();
                    updateAccountUI();
                }
            ).value()
        );
        accountText.setMovementMethod(LinkMovementMethod.getInstance());

        final CircularProgressDrawable placeholder = new CircularProgressDrawable(
            accountText.getContext()
        );
        placeholder.setStyle(LARGE);
        ImageView icon = getView().findViewById(R.id.backup_userIcon);
        Glide.with(this)
            .load(account.getPhotoUrl())
            .error(getResources().getDrawable(R.drawable.ic_user, null))
            .placeholder(placeholder)
            .apply(RequestOptions.circleCropTransform())
            .into(icon);
    }

    private void initDrive() {
        getView().findViewById(R.id.backup_backupBtn).setOnClickListener(view ->
            DriveBackupService.enqueueBackup(view.getContext())
        );
        getView().findViewById(R.id.backup_restoreBtn).setOnClickListener(view -> {
                new AlertDialog.Builder(view.getContext())
                    .setMessage(R.string.Restore_from_Google_Drive_will_remove_all_data_at_this_device_Proceed_)
                    .setPositiveButton(R.string.Yes, (dialog, which) ->
                        DriveBackupService.enqueueRestore(view.getContext())
                    ).setNegativeButton(R.string.No, (dialog, which) -> {
                }).show();
            }
        );
    }
}
