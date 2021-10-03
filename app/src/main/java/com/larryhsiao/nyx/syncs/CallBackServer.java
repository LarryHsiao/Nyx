package com.larryhsiao.nyx.syncs;

import android.os.Handler;
import com.larryhsiao.nyx.BuildConfig;
import com.larryhsiao.nyx.core.sync.dropbox.DBToken;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class CallBackServer extends NanoHTTPD {
    private final Handler mainHandler;
    private final Function<String, Void> success;
    public CallBackServer(int port, Handler mainHandler, Function<String, Void> success) {
        super(port);
        this.mainHandler = mainHandler;
        this.success = success;
    }
    @Override
    public Response serve(IHTTPSession session) {
        String msg = loginSuccessHtml();
        // language=HTML
        final List<String> code = session.getParameters().get("code");
        if (code == null || code.size() == 0) {
            throw new IllegalArgumentException("Auth code fetching failure");
        }
        final String token = new DBToken(
            BuildConfig.DROPBOX_API_TOKEN,
            code.get(0)
        ).value();
        mainHandler.post(() -> success.apply(token));
        stopByDelay(this, 5000);
        return newFixedLengthResponse(msg);
    }

    @Override
    public void start() throws IOException {
        super.start();
        stopByDelay(this,30000);
    }


    private void stopByDelay(NanoHTTPD server ,int delay){
        mainHandler.postDelayed(server::stop, delay);
    }

    private String loginSuccessHtml(){
        return  "<!DOCTYPE html>\n" +
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
    }
}
