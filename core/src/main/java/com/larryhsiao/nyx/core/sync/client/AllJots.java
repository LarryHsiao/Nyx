package com.larryhsiao.nyx.core.sync.client;

import com.larryhsiao.clotho.Source;

import javax.json.Json;
import javax.json.JsonArray;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_JOTS;

/**
 * Source to build Json array of jots from remote server.
 */
public class AllJots implements Source<InputStream> {
    private final String host;

    public AllJots(String host) {
        this.host = host;
    }

    @Override
    public InputStream value() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(host + ENDPOINT_JOTS).openConnection();
            return conn.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
