package com.larryhsiao.nyx.core.sync.dropbox;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.clotho.io.ProgressedCopy;
import com.larryhsiao.clotho.stream.StreamString;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Action to upload file to  dropbox
 */
public class DBFileUploading implements Action {
    private static final String URL = "https://content.dropboxapi.com/2/files/upload";
    private final String token;
    private final String path;
    private final InputStream inputStream;

    public DBFileUploading(String token, String path, InputStream inputStream) {
        this.token = token;
        this.path = path;
        this.inputStream = inputStream;
    }

    @Override
    public void fire() {
        try {
            final HttpURLConnection conn = (HttpURLConnection) new URL(URL).openConnection();
            conn.addRequestProperty("Authorization", "Bearer " + token);
            conn.addRequestProperty("Content-Type", "application/octet-stream");
            conn.addRequestProperty("Dropbox-API-Arg", "{\"path\":\"" + path + "\",\"mode\":{\".tag\":\"overwrite\"}}");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.connect();
            final OutputStream outputStream = conn.getOutputStream();
            new ProgressedCopy(
                inputStream,
                outputStream,
                4096,
                true,
                progress -> null
            ).fire();
            inputStream.close();
            outputStream.close();
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException(
                    "Upload file failure: " + path + "\n" +
                        new StreamString(conn.getErrorStream()).value()
                );
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
