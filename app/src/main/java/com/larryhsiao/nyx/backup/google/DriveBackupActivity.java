package com.larryhsiao.nyx.backup.google;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.larryhsiao.nyx.base.JotActivity;

import static androidx.swiperefreshlayout.widget.CircularProgressDrawable.LARGE;
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
public class DriveBackupActivity extends JotActivity {
    private static final int REQUEST_CODE_SIGN_IN = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_backup);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(new Scope(DRIVE_FILE))
            .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
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

    private void updateAccountUI(GoogleSignInAccount account) {
        TextView accountText = findViewById(R.id.backup_accountInfo);
        accountText.setText("");
        accountText.append(account.getDisplayName());
        accountText.append("\n");
        accountText.append(account.getEmail());
        accountText.append("\n");

        final CircularProgressDrawable placeholder = new CircularProgressDrawable(
            accountText.getContext()
        );
        placeholder.setStyle(LARGE);
        ImageView icon = findViewById(R.id.backup_userIcon);
        Glide.with(this)
            .load(account.getPhotoUrl())
            .error(getResources().getDrawable(R.drawable.ic_user, null))
            .placeholder(placeholder)
            .apply(RequestOptions.circleCropTransform())
            .into(icon);
    }

    private void initDrive() {
        findViewById(R.id.backup_backupBtn).setOnClickListener(view ->
            DriveBackupService.enqueueBackup(this)
        );
        findViewById(R.id.backup_restoreBtn).setOnClickListener(view -> {
                new AlertDialog.Builder(view.getContext())
                    .setMessage(R.string.Restore_from_Google_Drive_will_remove_all_data_at_this_device_Proceed_)
                    .setPositiveButton(R.string.Yes, (dialog, which) ->
                        DriveBackupService.enqueueRestore(this)
                    ).setNegativeButton(R.string.No, (dialog, which) -> {
                }).show();
            }
        );
    }
}
