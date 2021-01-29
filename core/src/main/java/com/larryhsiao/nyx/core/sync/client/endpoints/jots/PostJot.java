package com.larryhsiao.nyx.core.sync.client.endpoints.jots;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.clotho.io.StringOutput;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.sync.json.jots.JotJson;

import java.net.HttpURLConnection;
import java.net.URL;

import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_JOTS;

/**
 * Update a exist jot create new one if not exist.
 * Note that there are no new id in response for further tracking the jot.
 */
public class PostJot implements Action {
    private final String host;
    private final Jot jot;

    public PostJot(String host, Jot jot) {
        this.host = host;
        this.jot = jot;
    }

    @Override
    public void fire() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(
                host + ENDPOINT_JOTS
            ).openConnection();
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Content-Type", "application/json");
            new StringOutput(
                new JotJson(jot).value().toString(),
                conn.getOutputStream()
            ).fire();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
