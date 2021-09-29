package com.larryhsiao.nyx.syncs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.larryhsiao.nyx.core.sync.SyncImpl;
import fi.iki.elonen.NanoHTTPD;

import java.util.function.Function;

public class DropboxLogin implements SyncImpl.Login {
    private static final String URL_CODE_FLOW =
        "https://www.dropbox.com/oauth2/authorize?client_id=%s&response_type=code&redirect_uri=http://localhost:9981/token";
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Context context;
    private final String apiKey;

    public DropboxLogin(Context context, String apiKey) {
        this.context = context;
        this.apiKey = apiKey;
    }

    @Override
    public void login(Function<String, Void> success) {
        launchHttpServer(success);
        final String authFlow = String.format(URL_CODE_FLOW, apiKey);
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(authFlow));
        context.startActivity(intent);
    }

    private void launchHttpServer(Function<String, Void> success) {
        try {
            NanoHTTPD server = new CallBackServer(9981, mainHandler, success);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
