package com.larryhsiao.nyx.sync;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import com.larryhsiao.nyx.core.attachments.AllAttachments;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.attachments.QueriedAttachments;
import com.larryhsiao.nyx.core.attachments.UpdateAttachment;
import com.larryhsiao.nyx.core.attachments.WrappedAttachment;
import com.silverhetch.aura.images.JpegCompress;
import com.silverhetch.aura.uri.UriMimeType;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.encryption.MD5;
import com.silverhetch.clotho.file.ToFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.larryhsiao.nyx.JotApplication.URI_FILE_PROVIDER;

/**
 * Action to sync the attachment files to internal to prevent permission losing.
 * Delete the file if attachment deleted.
 * <p>
 * The attachment files will be stored in attachment directory at app internal file root.
 */
public class LocalFileSync implements Action {
    private final MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
    private final Context context;
    private final Source<Connection> db;
    private final Function<Integer, Void> progress;

    public LocalFileSync(
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
        final File internalRoot = new File(context.getFilesDir(), "attachments");
        internalRoot.mkdir();
        final List<Attachment> dbAttachments = new QueriedAttachments(
            new AllAttachments(db, true)
        ).value()
            .stream()
            .filter(it -> !it.uri().isEmpty())
            .collect(Collectors.toList());
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
        final File temp = Files.createTempFile("temp", "").toFile();
        final File compressTemp = Files.createTempFile("compressedTemp", "").toFile();
        new ToFile(
            context.getContentResolver().openInputStream(Uri.parse(attachment.uri())),
            temp,
            it -> null
        ).fire();
        new JpegCompress(temp, compressTemp).fire();
        final String fileName = generateFileName(
            "jpg",
            new MD5(new FileInputStream(compressTemp)).value()
        );
        compressTemp.renameTo(new File(internalRoot, fileName));
        new UpdateAttachment(db, new WrappedAttachment(attachment) {
            @Override
            public String uri() {
                return URI_FILE_PROVIDER + fileName;
            }
        }).fire();
    }

    private String generateFileName(String ext, String md5) {
        return md5 + "-" + UUID.randomUUID().toString().substring(0, 7) + "." + ext;
    }

    private void copyToInternal(String ext, File internalRoot, Attachment attachment) throws IOException {
        final Uri uri = Uri.parse(attachment.uri());
        final String fileName = generateFileName(
            ext,
            new MD5(context.getContentResolver().openInputStream(uri)).value()
        );
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
    }
}
