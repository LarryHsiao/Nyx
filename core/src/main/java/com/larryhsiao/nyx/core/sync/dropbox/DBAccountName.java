package com.larryhsiao.nyx.core.sync.dropbox;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.io.ProgressedCopy;
import com.larryhsiao.clotho.stream.StreamString;
import com.larryhsiao.nyx.core.sync.Jwt;

import javax.json.Json;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Source to build Dropbox Account name.
 */
public class DBAccountName implements Source<String> {
    private static final String URL_TOKEN = "https://api.dropboxapi.com/2/users/get_account";
    private final Jwt jwt;

    public DBAccountName(Jwt jwt) {
        this.jwt = jwt;
    }

    @Override
    public String value() {
        try {
            final HttpURLConnection conn =
                ((HttpURLConnection) new URL(URL_TOKEN).openConnection());
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Authorization", jwt.accessToken());
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            new ProgressedCopy(
                new ByteArrayInputStream(
                    Json.createObjectBuilder()
                        .add("account_id", jwt.accountId())
                        .build()
                        .toString()
                        .getBytes()
                ),
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
            return Json.createReader(
                new StringReader(new StreamString(conn.getInputStream()).value())
            ).readObject()
                .getJsonObject("name")
                .getString("display_name");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
