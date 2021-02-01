package com.larryhsiao.nyx.core.sync.client.endpoints.tags;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.clotho.io.StringOutput;

import javax.json.Json;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_JOTS;
import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_TAGS;

/**
 * Delete the given Jot.
 */
public class DeleteTag implements Action {
    private final String host;
    private final long id;

    public DeleteTag(String host, long id) {
        this.host = host;
        this.id = id;
    }

    @Override
    public void fire() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(
                host + ENDPOINT_TAGS
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
