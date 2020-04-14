package com.larryhsiao.nyx.core.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.larryhsiao.nyx.core.attachments.AttachmentDb;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.JotsDb;
import com.silverhetch.clotho.database.h2.MemoryH2Conn;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.facets.fork.TkRegex;
import org.takes.http.FtBasic;
import org.takes.rs.RsHtml;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithType;

import java.io.IOException;

/**
 * Web Access implementation by Take
 */
public class TakeWebAccess implements WebAccess {
    private final Gson gson;
    private final ResourceFiles res;
    private boolean exit;

    public TakeWebAccess(ResourceFiles res, Gson gson) {
        this.res = res;
        this.gson = gson;
    }

    public TakeWebAccess(ResourceFiles res){
        this(res, new GsonBuilder().create());
    }

    @Override
    public void start() {
        try {
            new FtBasic(
                new TkFork(
                    new FkRegex("/lib/.*", (TkRegex) request ->
                        new RsWithType(
                            new RsWithBody(
                                res.open("web" + request.matcher().group())
                            ), "text/css"
                        )
                    ),
                    new FkRegex("/", (TkRegex) request ->
                        new RsHtml(res.open("web/index.html"))
                    ),
                    new FkRegex("/map.html", (TkRegex) request ->
                        new RsHtml(res.open("web/map.html"))
                    ),
                    new FkRegex("/api/jots",new TkJots(gson,new FakeDb(new AttachmentDb(new JotsDb(new MemoryH2Conn())))))
                ), 8080
            ).start(() -> exit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        exit = true;
    }
}
