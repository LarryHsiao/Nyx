package com.larryhsiao.nyx.core.sync.client;

import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.Jots;
import com.larryhsiao.nyx.core.sync.client.endpoints.jots.DeleteJot;
import com.larryhsiao.nyx.core.sync.client.endpoints.jots.GetJotById;
import com.larryhsiao.nyx.core.sync.client.endpoints.jots.GetJots;
import com.larryhsiao.nyx.core.sync.client.endpoints.jots.PostJot;
import com.larryhsiao.nyx.core.sync.client.endpoints.jots.PutJot;

import java.util.List;

/**
 * Jots from remote server via http.
 */
public class RemoteJots implements Jots {
    private final String host;

    public RemoteJots(String host) {
        this.host = host;
    }

    @Override
    public List<Jot> all() {
        return new GetJots(host).value();
    }

    @Override
    public Jot byId(long id) {
        return new GetJotById(host, id).value();
    }

    @Override
    public Jot create(Jot jot) {
        return new PutJot(host, jot).value();
    }

    @Override
    public void update(Jot jot) {
        new PostJot(host, jot).fire();
    }

    @Override
    public void deleteById(long id) {
        new DeleteJot(host, id).fire();
    }
}
