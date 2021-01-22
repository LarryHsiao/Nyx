package com.larryhsiao.nyx.core.attachments.file;

import java.io.File;

/**
 * Files of attachments.
 */
public interface AttachmentFiles {
    /**
     * Build {@link File} by uri.
     *
     * @param uri The attachment uri.
     * @return The {@link File} of the attachment.
     */
    File fileByUri(String uri);
}
