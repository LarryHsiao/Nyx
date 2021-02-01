package com.larryhsiao.nyx.core.sync.client.endpoints.jots;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.io.StringOutput;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.WrappedJot;
import com.larryhsiao.nyx.core.sync.json.jots.JotJson;

import javax.json.Json;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_JOTS;

/**
 * Create a new Jot, will return a {@link Jot} with new id.
 */
public class PutJot implements Source<Jot> {
    private final String host;
    private final Jot jot;

    public PutJot(String host, Jot jot) {
        this.host = host;
        this.jot = jot;
    }

    @Override
    public Jot value() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(
                host + ENDPOINT_JOTS
            ).openConnection();
            conn.setRequestMethod("PUT");
            conn.addRequestProperty("Content-Type", "application/json");
            new StringOutput(
                new JotJson(jot).value().toString(),
                conn.getOutputStream()
            ).fire();
            final long id = Json.createReader(
                conn.getInputStream()
            ).readObject().getJsonNumber("id").longValue();
            return new WrappedJot(jot) {
                @Override
                public long id() {
                    return id;
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
