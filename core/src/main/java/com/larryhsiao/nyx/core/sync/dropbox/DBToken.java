package com.larryhsiao.nyx.core.sync.dropbox;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.io.ProgressedCopy;
import com.larryhsiao.clotho.stream.StreamString;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Source to build Dropbox token json.
 */
public class DBToken implements Source<String> {
    private static final String URL_TOKEN = "https://api.dropbox.com/oauth2/token";
    private static final String BODY = "code=%s&grant_type=authorization_code&redirect_uri=http://localhost:9981/token";
    private final String code;

    public DBToken(String code) {
        this.code = code;
    }

    @Override
    public String value() {
        try {
            final HttpURLConnection conn =
                ((HttpURLConnection) new URL(URL_TOKEN).openConnection());
            conn.setRequestMethod("POST");
            conn.addRequestProperty(
                "Authorization",
                "Basic dXhxbDNla3hnODJqOGFxOjV3bW1lbnZlNGFvMjc4ZA=="
            );
            conn.setDoOutput(true);
            new ProgressedCopy(
                new ByteArrayInputStream(String.format(BODY, code).getBytes()),
                conn.getOutputStream(),
                4096,
                (progress) -> null
            ).fire();
            conn.connect();
            if (conn.getResponseCode() != 200) {
                throw new IllegalArgumentException(
                    "Fetch Dropbox token failure: " +
                        new StreamString(conn.getErrorStream()).value()
                );
            }
            return new StreamString(conn.getInputStream()).value();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
