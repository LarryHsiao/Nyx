package com.larryhsiao.nyx;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.larryhsiao.nyx.core.web.TakeWebAccess;
import com.larryhsiao.nyx.core.web.WebAccess;
import com.larryhsiao.nyx.web.AndroidResourceFiles;

public class DebugActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.debug_panel);

        WebAccess webAccess = new TakeWebAccess(
            new AndroidResourceFiles(this)
        );
        new Thread(webAccess::start).start();
    }
}
