package com.larryhsiao.nyx.core.sync.client.endpoints.tags;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.sync.json.tags.JsonJotTags;
import com.larryhsiao.nyx.core.sync.json.tags.JsonTags;
import com.larryhsiao.nyx.core.tags.JotTag;

import javax.json.Json;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_JOT_TAGS;

/**
 * Source to build JotTags from requesting to a remote Nyx instance.
 */
public class GetJotTags implements Source<List<JotTag>> {
    private final String host;

    public GetJotTags(String host) {
        this.host = host;
    }

    @Override
    public List<JotTag> value() {
        try {
            final HttpURLConnection conn = (HttpURLConnection) new URL(
                host + ENDPOINT_JOT_TAGS
            ).openConnection();
            final List<JotTag> result = new JsonJotTags(
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
