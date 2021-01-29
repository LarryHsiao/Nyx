package com.larryhsiao.nyx.core.sync.server;

import com.larryhsiao.nyx.core.Nyx;
import org.takes.Response;
import org.takes.facets.auth.PsEmpty;
import org.takes.facets.auth.TkAuth;
import org.takes.facets.fork.*;
import org.takes.http.FtBasic;
import org.takes.rq.RqMethod;
import org.takes.rs.RsWithStatus;

import java.io.IOException;

/**
 * Nyx server for open the jot datas.
 */
public class NyxServer {
    public static final String ENDPOINT_JOTS = "/jots";
    private final Nyx nyx;
    private boolean isRunning = false;

    public NyxServer(Nyx nyx) {
        this.nyx = nyx;
    }

    public void launch() throws IOException {
        if (isRunning) {
            return;
        }
        isRunning = true;
        // @todo #106 Nyx Server
        new FtBasic(
            new TkAuth(
                new TkFork(
                    new FkRegex(
                        ENDPOINT_JOTS,
                        new TkFork(
                            new FkMethods("GET", new TkJots(nyx)),
                            new FkMethods("PUT", new TkNewJot(nyx)),
                            new FkMethods("DELETE", new TkDeleteJot(nyx)),
                            new FkMethods("POST", new TkUpdateJot(nyx))
                        )
                    ),
                    new FkRegex("/attachments", new TkAttachments(nyx)),
                    new FkRegex(
                        "/attachments/download/(?<id>[^/]+)",
                        new TkAttachmentDownloading(nyx)
                    )
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
