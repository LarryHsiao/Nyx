package com.larryhsiao.nyx.core.sync.client.endpoints;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.sync.client.JsonJots;

import javax.json.Json;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_JOTS;

/**
 * Source to build Json array of jots from remote server.
 */
public class GetJots implements Source<List<Jot>> {
    private final String host;

    public GetJots(String host) {
        this.host = host;
    }

    @Override
    public List<Jot> value() {
        try {
            final HttpURLConnection conn = (HttpURLConnection) new URL(host + ENDPOINT_JOTS).openConnection();
            final List<Jot> result = new JsonJots(
                Json.createParser(conn.getInputStream()).getArray()
            ).value();
            conn.disconnect();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
