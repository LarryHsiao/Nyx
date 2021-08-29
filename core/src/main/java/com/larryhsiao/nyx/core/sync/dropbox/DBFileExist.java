package com.larryhsiao.nyx.core.sync.dropbox;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.io.ProgressedCopy;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Check if file exist. Get Metadata of file for checking the existance.
 */
public class DBFileExist implements Source<Boolean> {
    private static final String URL = "https://api.dropboxapi.com/2/files/get_metadata";
    private final String token;
    private final String path;

    public DBFileExist(String token, String path) {
        this.token = token;
        this.path = path;
    }

    @Override
    public Boolean value() {
        try {
            final HttpURLConnection conn = (HttpURLConnection) new URL(URL).openConnection();
            conn.addRequestProperty("Authorization", "Bearer " + token);
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.connect();
            new ProgressedCopy(
                new ByteArrayInputStream(("{\"path\":\"" + path + "\"}").getBytes(UTF_8)),
                conn.getOutputStream(),
                4096,
                true,
                progress -> null
            ).fire();
            return conn.getResponseCode() == 200;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
