package com.larryhsiao.nyx.core.sync.dropbox;

import com.larryhsiao.clotho.Source;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Source to build InputStream of file at Dropbox
 */
public class DBFileStream implements Source<InputStream> {
    private static final String URL = "https://content.dropboxapi.com/2/files/download";
    private final String token;
    private final String path;

    public DBFileStream(String token, String path) {
        this.token = token;
        this.path = path;
    }

    @Override
    public InputStream value() {
        try {
            final HttpURLConnection conn = (HttpURLConnection) new URL(URL).openConnection();
            conn.addRequestProperty("Authorization", "Bearer " + token);
            conn.addRequestProperty("Dropbox-API-Arg", "{\"path\":\"" + path + "\"}");
            conn.connect();
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Download file failure: " + path);
            }
            return conn.getInputStream();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
