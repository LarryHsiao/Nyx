package com.larryhsiao.nyx.core.sync.server;

import com.larryhsiao.clotho.file.FileMimeType;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.attachments.AttachmentById;
import org.takes.Response;
import org.takes.facets.fork.RqRegex;
import org.takes.facets.fork.TkRegex;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Take for downloading the attachment.
 */
public class TkAttachmentDownloading implements TkRegex {
    private final Nyx nyx;

    public TkAttachmentDownloading(Nyx nyx) {
        this.nyx = nyx;
    }

    @Override
    public Response act(RqRegex req) throws IOException {
        final File file = nyx.files().fileByUri(
            nyx.attachments().byId(Long.parseLong(req.matcher().group(1))).uri()
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
