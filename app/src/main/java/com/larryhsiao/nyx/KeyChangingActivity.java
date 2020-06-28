package com.larryhsiao.nyx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.firebase.auth.FirebaseAuth;
import com.larryhsiao.nyx.account.api.ChangeEncryptKeyReq;
import com.larryhsiao.nyx.account.api.NyxApi;
import com.larryhsiao.nyx.base.JotActivity;
import com.larryhsiao.nyx.sync.SyncService;
import com.silverhetch.aura.view.dialog.InputDialog;
import com.silverhetch.clotho.encryption.MD5;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayInputStream;

import static android.widget.Toast.LENGTH_SHORT;
import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.larryhsiao.nyx.sync.SyncService.enqueue;

/**
 * Activity to let user to change the encryption key which is currently conflicted to cloud one.
 */
public class KeyChangingActivity extends JotActivity implements InputDialog.Callback {
    private final static int REQUEST_CODE_NEW_KEY = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_encrypt_key);

        findViewById(R.id.changEncryptKey_changeLocal)
            .setOnClickListener(v -> InputDialog.Companion
                .newInstance(getString(R.string.Enter_new_key), REQUEST_CODE_NEW_KEY)
                .show(getSupportFragmentManager(), null));

        findViewById(R.id.changEncryptKey_deleteRemote).setOnClickListener(v -> {
            final SharedPreferences pref = getDefaultSharedPreferences(this);
            final String encryptKey = pref.getString("encrypt_key", "");
            final ChangeEncryptKeyReq req = new ChangeEncryptKeyReq();
            req.keyHash = new MD5(new ByteArrayInputStream(encryptKey.getBytes())).value();
            req.uid = FirebaseAuth.getInstance().getUid();
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.fui_progress_dialog_loading));
            dialog.setCancelable(false);
            dialog.show();
            NyxApi.client().changeEncryptKey(req).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        enqueue(KeyChangingActivity.this);
                    } else {
                        Toast.makeText(
                            KeyChangingActivity.this,
                            R.string.appError_unknown,
                            LENGTH_SHORT
                        ).show();
                    }
                    finish();
                }

                @Override
                public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                    Toast.makeText(
                        KeyChangingActivity.this,
                        R.string.appError_unknown,
                        LENGTH_SHORT
                    ).show();
                    finish();
                }
            });
        });
    }

    @Override
    public void onInputDialogResult(int requestCode, int result, @NotNull Intent intent) {
        if (requestCode == REQUEST_CODE_NEW_KEY && result == RESULT_OK) {
            final SharedPreferences pref = getDefaultSharedPreferences(this);
            pref.edit().putString("encrypt_key", intent.getStringExtra("INPUT_FIELD")).commit();
            SyncService.enqueue(this);
            finish();
        }
    }
}
