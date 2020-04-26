package com.larryhsiao.nyx.attachments;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import com.larryhsiao.nyx.core.attachments.AllAttachments;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.attachments.QueriedAttachments;
import com.larryhsiao.nyx.core.attachments.UpdateAttachment;
import com.larryhsiao.nyx.core.attachments.WrappedAttachment;
import com.silverhetch.aura.uri.UriMimeType;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.encryption.MD5;
import com.silverhetch.clotho.file.ToFile;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.larryhsiao.nyx.JotApplication.URI_FILE_PROVIDER;

/**
 * Action to copy the attachment files to internal to prevent permission losing.
 * <p>
 * The attachment files will be stored in attachment directory at app internal file root.
 * @todo #4 Image compression.
 */
public class CopyToInternal implements Action {
    private final MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
    private final Context context;
    private final Source<Connection> db;
    private final Function<Integer, Void> progress;

    public CopyToInternal(
        Context context,
        Source<Connection> db,
        Function<Integer, Void> progress
    ) {
        this.context = context;
        this.db = db;
        this.progress = progress;
    }

    @Override
    public void fire() {
        final List<Attachment> copyRequired = new ArrayList<>();
        for (Attachment attachment : new QueriedAttachments(
            new AllAttachments(db, true)
        ).value()) {
            if (!attachment.uri().startsWith(URI_FILE_PROVIDER)) {
                copyRequired.add(attachment);
            }
        }

        final File internalRoot = new File(context.getFilesDir(), "attachments");
        internalRoot.mkdir();
        for (int i = 0; i < copyRequired.size(); i++) {
            try {
                final Attachment attachment = copyRequired.get(i);
                final Uri uri = Uri.parse(attachment.uri());
                final String md5 = new MD5(context.getContentResolver().openInputStream(uri)).value();
                final String ext = mimeTypeMap.getExtensionFromMimeType(
                    new UriMimeType(context, uri.toString()).value()
                );
                final String fileName = md5 + "." + ext;
                new ToFile(
                    context.getContentResolver().openInputStream(uri),
                    new File(internalRoot, fileName),
                    integer -> null
                ).fire();
                new UpdateAttachment(db, new WrappedAttachment(attachment) {
                    @Override
                    public String uri() {
                        return URI_FILE_PROVIDER + fileName;
                    }
                }).fire();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
