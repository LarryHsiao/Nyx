package com.larryhsiao.nyx.syncs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.larryhsiao.clotho.storage.Ceres;
import com.larryhsiao.nyx.core.sync.SyncImpl;
import com.larryhsiao.nyx.core.sync.dropbox.DBToken;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.List;
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
        try {
            NanoHTTPD server = new NanoHTTPD(9981) {
                @Override
                public Response serve(IHTTPSession session) {
                    String msg =
                        // language=HTML
                        "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "  \n" +
                            "<body>\n" +
                            "    <h1 style=\"color: black;\">\n" +
                            "        \n" +
                            "    </h1>\n" +
                            "  \n" +
                            "    <p>\n" +
                            "        You are logged in.\n" +
                            "    </p>\n" +
                            "  \n" +
                            "    <!-- Define the button to \n" +
                            "       close the window -->\n" +
                            "    <button onclick=\"return backToApp();\">\n" +
                            "        Done \n" +
                            "    </button>\n" +
                            "      \n" +
                            "    <script type=\"text/javascript\">\n" +
                            "        function backToApp() {\n" +
                            "            return false;\n" +
                            "        }\n" +
                            "    </script>\n" +
                            "</body>\n" +
                            "  \n" +
                            "</html>";
                    final List<String> code = session.getParameters().get("code");
                    if (code == null || code.size() == 0){
                        throw new IllegalArgumentException("Auth code fetching failure");
                    }
                    final String token = new DBToken(code.get(0)).value();
                    mainHandler.post(() -> success.apply(token));
                    stopByDelay(5000);
                    return newFixedLengthResponse(msg);
                }

                private void stopByDelay(int delay){
                    mainHandler.postDelayed(this::stop, delay);
                }

                @Override
                public void start() throws IOException {
                    super.start();
                    stopByDelay(30000);
                }
            };
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String authFlow = String.format(URL_CODE_FLOW, apiKey);
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(authFlow));
        context.startActivity(intent);
    }
}
