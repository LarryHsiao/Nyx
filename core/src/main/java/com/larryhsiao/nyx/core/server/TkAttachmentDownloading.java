package com.larryhsiao.nyx.core.server;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.file.FileMimeType;
import com.larryhsiao.nyx.core.attachments.AttachmentById;
import com.larryhsiao.nyx.core.attachments.file.AttachmentFiles;
import org.takes.Response;
import org.takes.facets.fork.RqRegex;
import org.takes.facets.fork.TkRegex;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;

/**
 * Take for downloading the attachment.
 */
public class TkAttachmentDownloading implements TkRegex {
    private final Source<Connection> db;
    private final AttachmentFiles files;

    public TkAttachmentDownloading(Source<Connection> db, AttachmentFiles files) {
        this.db = db;
        this.files = files;
    }

    @Override
    public Response act(RqRegex req) throws IOException {
        final File file = files.fileByUri(
            new AttachmentById(
                db,
                Long.parseLong(
                    req.matcher().group(1)
                )
            ).value().uri()
        );
        return new RsWithType(
            new RsWithBody(
                new FileInputStream(
                    file
                )
            ),
            new FileMimeType(file).value()
        );
    }
}
