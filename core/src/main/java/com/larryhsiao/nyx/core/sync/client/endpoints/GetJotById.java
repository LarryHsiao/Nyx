package com.larryhsiao.nyx.core.sync.client.endpoints;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.io.StringOutput;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.sync.client.JsonJot;
import com.larryhsiao.nyx.core.sync.client.JsonJots;

import javax.json.Json;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_JOTS;

/**
 * Source to build Json array of jots from remote server.
 */
public class GetJotById implements Source<Jot> {
    private final String host;
    private final long id;

    public GetJotById(String host, long id) {
        this.host = host;
        this.id = id;
    }

    @Override
    public Jot value() {
        try {
            final HttpURLConnection conn = (HttpURLConnection) new URL(
                host + ENDPOINT_JOTS
            ).openConnection();
            new StringOutput(
                Json.createObjectBuilder()
                    .add("id", id)
                    .build().toString(),
                conn.getOutputStream()
            ).fire();
            final Jot result = new JsonJot(
                Json.createParser(conn.getInputStream()).getObject()
            );
            conn.disconnect();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
