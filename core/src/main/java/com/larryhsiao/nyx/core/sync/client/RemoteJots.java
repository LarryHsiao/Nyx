package com.larryhsiao.nyx.core.sync.client;

import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.Jots;
import com.larryhsiao.nyx.core.sync.client.endpoints.*;

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
    public Jot newJot(Jot jot) {
        return new PutJot(host, jot).value();
    }

    @Override
    public void updateJot(Jot jot) {
        new PostJot(host, jot).fire();
    }

    @Override
    public void deleteJotById(long id) {
        new DeleteJot(host, id).fire();
    }
}
