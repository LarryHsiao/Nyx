package com.larryhsiao.nyx.attachment;

import android.content.Context;
import android.net.Uri;
import com.larryhsiao.nyx.core.attachments.file.NyxFiles;
import com.larryhsiao.nyx.old.attachments.AttachmentFileSource;

import java.io.File;

/**
 * Attachment Files in the storage.
 */
public class NyxFilesImpl implements NyxFiles {
    private final Context context;

    public NyxFilesImpl(Context context) {
        this.context = context;
    }

    @Override
    public File fileByUri(String uri) {
        return new AttachmentFileSource(context, Uri.parse(uri)).value();
    }
}
