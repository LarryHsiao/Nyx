package com.larryhsiao.nyx.core.sync.dropbox;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.clotho.Source;
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
    private final Source<InputStream> streamSource;
    private int retryCounter = 0;

    public DBFileUploading(String token, String path, Source<InputStream> streamSource) {
        this.token = token;
        this.path = path;
        this.streamSource = streamSource;
    }

    @Override
    public void fire() {
        try {
            final InputStream stream = streamSource.value();
            final HttpURLConnection conn = (HttpURLConnection) new URL(URL).openConnection();
            conn.addRequestProperty("Authorization", "Bearer " + token);
            conn.addRequestProperty("Content-Type", "application/octet-stream");
            conn.addRequestProperty("Dropbox-API-Arg",
                "{\"path\":\"" + path + "\",\"mode\":{\".tag\":\"overwrite\"}}");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.connect();
            final OutputStream outputStream = conn.getOutputStream();
            new ProgressedCopy(
                stream,
                outputStream,
                4096,
                true,
                progress -> null
            ).fire();
            stream.close();
            outputStream.close();
            if (conn.getResponseCode() != 200) {
                final String errorMsg = new DBJsonError(
                    new StreamString(conn.getErrorStream())
                ).value();
                if (errorMsg.startsWith("too_many_write_operations/")) {
                    waitForRetry();
                    fire();
                } else if (retryCounter < 3) {
                    retryCounter++;
                    waitForRetry();
                    fire();
                } else {
                    throw new RuntimeException("Upload file failure: " + path + "\n" + errorMsg);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void waitForRetry() {
        try {
            Thread.sleep(3000L);
        } catch (Exception ignore) {
        }
    }
}
