package com.larryhsiao.nyx.core.sync.client.endpoints;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.io.StringOutput;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.WrappedJot;
import com.larryhsiao.nyx.core.sync.server.JotJson;

import javax.json.Json;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_JOTS;

/**
 * Delete the given Jot.
 */
public class DeleteJot implements Action {
    private final String host;
    private final long id;

    public DeleteJot(String host, long id) {
        this.host = host;
        this.id = id;
    }

    @Override
    public void fire() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(
                host + ENDPOINT_JOTS
            ).openConnection();
            conn.setRequestMethod("DELETE");
            new StringOutput(
                Json.createObjectBuilder()
                    .add("id", id)
                    .build().toString(),
                conn.getOutputStream()
            ).fire();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
