package com.larryhsiao.nyx.core.sync.dropbox;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.clotho.io.ProgressedCopy;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DBFileDeletion implements Action {
    private static final String URL = "https://api.dropboxapi.com/2/files/delete_v2";
    private final String token;
    private final String path;

    public DBFileDeletion(String token, String path) {
        this.token = token;
        this.path = path;
    }

    @Override
    public void fire() {
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
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Delete file failure: code: "+conn.getResponseCode()+", path: " + path);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
