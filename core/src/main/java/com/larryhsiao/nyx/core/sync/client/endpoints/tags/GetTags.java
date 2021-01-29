package com.larryhsiao.nyx.core.sync.client.endpoints.tags;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.sync.json.tags.JsonTags;
import com.larryhsiao.nyx.core.tags.Tag;

import javax.json.Json;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_JOTS;
import static com.larryhsiao.nyx.core.sync.server.NyxServer.ENDPOINT_TAGS;

/**
 * Source to build Json array of jots from remote server.
 */
public class GetTags implements Source<List<Tag>> {
    private final String host;

    public GetTags(String host) {
        this.host = host;
    }

    @Override
    public List<Tag> value() {
        try {
            final HttpURLConnection conn = (HttpURLConnection) new URL(
                host + ENDPOINT_TAGS
            ).openConnection();
            final List<Tag> result = new JsonTags(
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
