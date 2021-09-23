package com.larryhsiao.nyx.syncs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.larryhsiao.nyx.core.sync.SyncImpl;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class DropboxLogin implements SyncImpl.Login {
    private static final String URL_CODE_FLOW =
        "https://www.dropbox.com/oauth2/authorize?client_id=%s&response_type=code&redirect_uri=http://localhost:9981/token";
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
                    if (code !=null) {
                        // @todo #1 Exchange access token
                    }
                    stopByDelay(5000);
                    return newFixedLengthResponse(msg);
                }

                private void stopByDelay(int delay){
                    new Thread(() -> {
                        try {
                            Thread.sleep(delay);
                            stop();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }).start();
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
        final String authFlow = String.format(
            URL_CODE_FLOW,
            apiKey
        );
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(authFlow));
        context.startActivity(intent);
    }
}
