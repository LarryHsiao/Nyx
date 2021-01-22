package com.larryhsiao.nyx.attachment;

import android.content.Context;
import android.net.Uri;
import com.larryhsiao.nyx.core.attachments.file.AttachmentFiles;
import com.larryhsiao.nyx.old.attachments.AttachmentFileSource;

import java.io.File;

/**
 * Attachment Files in the storage.
 */
public class AttachmentFilesImpl implements AttachmentFiles {
    private final Context context;

    public AttachmentFilesImpl(Context context) {
        this.context = context;
    }

    @Override
    public File fileByUri(String uri) {
        return new AttachmentFileSource(context, Uri.parse(uri)).value();
    }
}
