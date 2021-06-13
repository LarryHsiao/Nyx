package com.larryhsiao.nyx.attachment;

import android.content.Context;
import android.net.Uri;
import com.larryhsiao.clotho.Source;

import java.io.File;

import static com.larryhsiao.nyx.NyxApplication.URI_FILE_PROVIDER;

/**
 * Source to build File from given attachment uri.
 */
public class AttachmentFileSource implements Source<File> {
    private final Context context;
    private final Uri attachmentUri;

    public AttachmentFileSource(Context context, Uri attachmentUri) {
        this.context = context;
        this.attachmentUri = attachmentUri;
    }

    @Override
    public File value() {
        return new File(
            new File(context.getFilesDir(), "attachments"),
            attachmentUri.toString().replace(URI_FILE_PROVIDER, "")
        );
    }
}
