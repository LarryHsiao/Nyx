package com.larryhsiao.nyx;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.larryhsiao.nyx.backup.google.DriveBackupActivity;
import com.larryhsiao.nyx.base.JotActivity;

/**
 * Debug Panel activity
 */
public class DebugActivity extends JotActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, DriveBackupActivity.class));
    }
}
