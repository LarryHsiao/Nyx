package com.larryhsiao.nyx.core.sync.client.endpoints.tags;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.clotho.io.StringOutput;
import com.larryhsiao.nyx.core.sync.json.tags.JotTagJson;
import com.larryhsiao.nyx.core.tags.JotTag;

import java.net.HttpURLConnection;
import java.net.URL;

import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_JOT_TAGS;

/**
 * Action to fire a POST request to remote Nyx instance for creating/update a link.
 */
public class PutJotTags implements Action {
    private final String host;
    private final JotTag jotTag;

    public PutJotTags(String host, JotTag jotTag) {
        this.host = host;
        this.jotTag = jotTag;
    }

    @Override
    public void fire() {
        try {
            final HttpURLConnection conn = (HttpURLConnection) new URL(
                host + ENDPOINT_JOT_TAGS
            ).openConnection();
            conn.setRequestMethod("PUT");
            conn.addRequestProperty("Content-Type", "application/json");
            new StringOutput(
                new JotTagJson(jotTag).value().toString(),
                conn.getOutputStream()
            ).fire();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
