package com.larryhsiao.nyx.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.larryhsiao.nyx.NyxApplication;
import com.larryhsiao.nyx.attachment.NyxFilesImpl;
import com.larryhsiao.nyx.core.sync.server.NyxServer;

/**
 * Server for serving Jots.
 */
public class ServerService extends Service {
    private final NyxServer server = new NyxServer(() ->
        ((NyxApplication) getApplicationContext()).getDb().value(),
        new NyxFilesImpl(this),
        nyx);

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(() -> {
            try {
                server.launch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        server.shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
