package com.larryhsiao.nyx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.larryhsiao.nyx.old.base.JotActivity;
import com.larryhsiao.nyx.old.capture.CaptureActivity;

/**
 * Debug Panel activity
 */
public class DebugActivity extends JotActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_debug);
        startActivityForResult(
            new Intent(this, CaptureActivity.class),
            1000
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Toast.makeText(this, "fff", Toast.LENGTH_SHORT).show();
        }
    }
}
