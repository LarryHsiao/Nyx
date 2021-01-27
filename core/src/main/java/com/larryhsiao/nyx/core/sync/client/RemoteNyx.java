package com.larryhsiao.nyx.core.sync.client;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.attachments.Attachments;
import com.larryhsiao.nyx.core.attachments.file.NyxFiles;
import com.larryhsiao.nyx.core.jots.Jots;

// @todo #110 Complete this
/**
 * Remote implementation of {@link Nyx}.
 * Which fetch the Jots from another Nyx application.
 */
public class RemoteNyx implements Nyx {
    private final String host;

    public RemoteNyx(String host) {
        this.host = host;
    }

    @Override
    public Jots jots() {
        return new RemoteJots(host);
    }

    @Override
    public Attachments attachments() {
        return null;
    }

    @Override
    public NyxFiles files() {
        return null;
    }
}
