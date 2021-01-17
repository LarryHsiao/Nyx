package com.larryhsiao.nyx.core.server;

import com.larryhsiao.clotho.Source;
import org.takes.facets.auth.PsEmpty;
import org.takes.facets.auth.TkAuth;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.FtBasic;

import java.io.IOException;
import java.sql.Connection;

/**
 * Nyx server for open the jot datas.
 */
public class NyxServer {
    private final Source<Connection> db;
    private boolean isRunning = false;

    public NyxServer(Source<Connection> db) {
        this.db = db;
    }

    public void launch() throws IOException {
        if (isRunning) {
            return;
        }
        isRunning = true;
        new FtBasic(
            new TkAuth(
                new TkFork(
                    new FkRegex("/jots", new TkJots(db)),
                    new FkRegex("/attachments", new TkAttachments(db))
                ),
                new PsEmpty()
            ),
            8080
        ).start(() -> !isRunning);
    }

    public void shutdown() {
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
