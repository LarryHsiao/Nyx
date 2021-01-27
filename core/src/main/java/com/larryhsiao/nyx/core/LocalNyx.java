package com.larryhsiao.nyx.core;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.attachments.Attachments;
import com.larryhsiao.nyx.core.attachments.file.NyxFiles;
import com.larryhsiao.nyx.core.jots.Jots;
import com.larryhsiao.nyx.core.jots.LocalJots;

import java.sql.Connection;

// @todo #111 Complete this

/**
 * Implementation of local storing data.
 */
public class LocalNyx implements Nyx {
    private final Source<Connection> db;
    private final NyxFiles nyxFiles;

    public LocalNyx(Source<Connection> db, NyxFiles nyxFiles) {
        this.db = db;
        this.nyxFiles = nyxFiles;
    }

    @Override
    public Jots jots() {
        return new LocalJots(db);
    }

    @Override
    public Attachments attachments() {
        return null;
    }

    @Override
    public NyxFiles files() {
        return nyxFiles;
    }
}
