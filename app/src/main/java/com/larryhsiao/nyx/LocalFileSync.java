package com.larryhsiao.nyx;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import com.larryhsiao.clotho.dgist.MD5;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.attachments.*;
import com.larryhsiao.nyx.attachment.TempAttachmentFile;
import com.larryhsiao.aura.images.JpegCompress;
import com.larryhsiao.aura.uri.UriMimeType;
import com.larryhsiao.clotho.Action;
import com.larryhsiao.clotho.file.ToFile;
import com.larryhsiao.clotho.source.SingleRefSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.larryhsiao.nyx.NyxApplication.URI_FILE_PROVIDER;
import static com.larryhsiao.nyx.NyxApplication.URI_FILE_TEMP_PROVIDER;
import static java.util.stream.Collectors.toList;

/**
 * Action to sync the attachment files to internal to prevent permission losing.
 * Delete the file if attachment deleted.
 * <p>
 * The attachment files will be stored in attachment directory at app internal file root.
 */
public class LocalFileSync implements Action {
    private final MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
    private final Context context;
    private final Nyx nyx;

    public LocalFileSync(Context context, Nyx nyx) {
        this.context = context;
        this.nyx =nyx;
    }

    @Override
    public void fire() {
        final File internalRoot = new File(context.getFilesDir(), "attachments");
        internalRoot.mkdir();
        final List<Attachment> dbAttachments = nyx.attachments().all()
            .stream()
            .filter(it -> !it.uri().isEmpty())
            .collect(toList());
        for (Attachment attachment : dbAttachments) {
            if (attachment.uri().startsWith("content:")) {
                check(internalRoot, attachment);
            }
        }
    }

    private void check(File internalRoot, Attachment attachment) {
        if (attachment.uri().startsWith(URI_FILE_PROVIDER)) {
            checkDeleted(internalRoot, attachment);
        } else {
            copyToInternal(internalRoot, attachment);
        }
    }

    private void copyToInternal(File internalRoot, Attachment attachment) {
        try {
            final String ext = mimeTypeMap.getExtensionFromMimeType(
                new UriMimeType(context, attachment.uri()).value()
            );
            if ("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext)) {
                compressToInternal(internalRoot, attachment);
            } else {
                copyToInternal(ext, internalRoot, attachment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkDeleted(File internalRoot, Attachment attachment) {
        if (attachment.deleted()) {
            File internalFile = new File(
                internalRoot,
                attachment.uri().replaceFirst(URI_FILE_PROVIDER, "")
            );
            if (internalFile.exists()) {
                internalFile.delete();
            }
        }
    }

    private void compressToInternal(File internalRoot, Attachment attachment) throws IOException {
        final File compressTemp = new TempAttachmentFile(context, "compressedTemp").value();
        final File temp;
        if (attachment.uri().startsWith(URI_FILE_TEMP_PROVIDER)) {
            temp = new TempAttachmentFile(
                context,
                attachment.uri().replace(URI_FILE_TEMP_PROVIDER, "")
            ).value();
        } else {
            temp = new TempAttachmentFile(context, "temp").value();
            new ToFile(
                context.getContentResolver().openInputStream(Uri.parse(attachment.uri())),
                temp,
                it -> null
            ).fire();
        }
        new JpegCompress(temp, compressTemp, BuildConfig.JPG_COMPRESS_QUALITY).fire();
        temp.delete();
        final String fileName = generateFileName(
            "jpg",
            new MD5(new FileInputStream(compressTemp)).value()
        );
        compressTemp.renameTo(new File(internalRoot, fileName));
        nyx.attachments().update(new WrappedAttachment(attachment) {
            @Override
            public String uri() {
                return URI_FILE_PROVIDER + fileName;
            }
        });
        temp.delete();
    }

    private String generateFileName(String ext, String md5) {
        return md5 + "-" + UUID.randomUUID().toString().substring(0, 7) + "." + ext;
    }

    private void copyToInternal(String ext, File internalRoot, Attachment attachment)
        throws IOException {
        final Uri uri = Uri.parse(attachment.uri());
        final String fileName = generateFileName(
            ext,
            new MD5(context.getContentResolver().openInputStream(uri)).value()
        );
        if (uri.toString().startsWith(URI_FILE_TEMP_PROVIDER)) {
            new TempAttachmentFile(
                context,
                uri.toString().replace(URI_FILE_TEMP_PROVIDER, "")
            ).value().renameTo(new File(internalRoot, fileName));
        } else {
            new ToFile(
                context.getContentResolver().openInputStream(uri),
                new File(internalRoot, fileName),
                integer -> null
            ).fire();
        }
        nyx.attachments().update(new WrappedAttachment(attachment) {
            @NotNull
            @Override
            public String uri() {
                return URI_FILE_PROVIDER + fileName;
            }
        });
    }
}
