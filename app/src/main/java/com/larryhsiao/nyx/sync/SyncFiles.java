package com.larryhsiao.nyx.sync;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.larryhsiao.nyx.JotApplication;
import com.larryhsiao.nyx.core.attachments.AllAttachments;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.attachments.ByUrl;
import com.larryhsiao.nyx.core.attachments.QueriedAttachments;
import com.larryhsiao.nyx.core.attachments.UpdateAttachment;
import com.larryhsiao.nyx.core.attachments.WrappedAttachment;
import com.silverhetch.aura.uri.UriMimeType;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static android.graphics.Bitmap.CompressFormat.JPEG;
import static androidx.core.content.FileProvider.getUriForFile;

/**
 * Action to sync files to Firebase storage.
 *
 * @todo #2 syncing progress indicator.
 * @todo #3 File size limitation
 * @todo #4 Image compression.
 */
public class SyncFiles implements Action {
    private static final String URI_PATH = "content://com.larryhsiao.nyx.fileprovider/attachments";
    private final MimeTypeMap mimeType = MimeTypeMap.getSingleton();
    private final Context context;
    private final Source<Connection> db;
    private final String uid;

    public SyncFiles(Context context, Source<Connection> db, String uid) {
        this.context = context;
        this.db = db;
        this.uid = uid;
    }

    @Override
    public void fire() {
        Map<String, Attachment> attachmentMap = new QueriedAttachments(
            new AllAttachments(db)
        ).value().stream().collect(Collectors.toMap(
            Attachment::uri,
            Function.identity(),
            (attachment, attachment2) -> attachment
        ));
        syncAttachment(
            FirebaseStorage.getInstance().getReference(uid),
            attachmentMap.values().iterator()
        );
    }

    private void syncAttachment(StorageReference remoteRoot, Iterator<Attachment> attachments) {
        if (attachments.hasNext()) {
            syncAttachment(remoteRoot, attachments.next(), attachments);
        } else {
            new SyncAttachments(context, uid, db, false).fire();
        }
    }

    private void syncAttachment(
        StorageReference remoteRoot,
        Attachment attachment,
        Iterator<Attachment> iterator
    ) {
        if (!attachment.uri().startsWith(URI_PATH)) {
            checkRemoteExist(mimeType, attachment, remoteRoot, iterator);
        } else {
            final String fileName = attachment.uri().replace(URI_PATH, "");
            final File localFile = new File(new File(
                context.getFilesDir(),
                "attachments"
            ), fileName);
            localFile.getParentFile().mkdirs();
            if (!localFile.exists()) {
                remoteRoot.child(fileName.replace(uid + "/", ""))
                    .getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        syncAttachment(remoteRoot, iterator);
                    }).addOnFailureListener(e -> {
                    syncAttachment(remoteRoot, iterator);
                });
            } else {
                syncAttachment(remoteRoot, iterator);
            }
        }
    }

    private void checkRemoteExist(
        MimeTypeMap map,
        Attachment attachment,
        StorageReference remoteRoot,
        Iterator<Attachment> localAttachments
    ) {
        final String ext = map.getExtensionFromMimeType(
            new UriMimeType(context, attachment.uri()).value()
        );
        StorageReference remoteAttachment = remoteRoot.child(
            UUID.randomUUID().toString() + "." + (ext == null ? "" : ext)
        );
        remoteAttachment.getMetadata().addOnSuccessListener(meta -> {
            // @todo #1 Handle if file changed, deleted.
            syncAttachment(remoteRoot, localAttachments); // remote exist, iterate next
        }).addOnFailureListener(e ->
            uploadToRemote(
                remoteRoot,
                remoteAttachment,
                attachment,
                ext,
                localAttachments
            )
        );
    }

    private void uploadToRemote(
        StorageReference remoteRoot,
        StorageReference remoteAttachment,
        Attachment attachment,
        String ext,
        Iterator<Attachment> localIterator
    ) {
        try {
            Uri localUri = Uri.parse(attachment.uri());
            remoteAttachment.putStream(uploadStream(
                context.getContentResolver().openInputStream(localUri),
                localUri,
                ext
            )).addOnSuccessListener(it ->
                ((JotApplication) context.getApplicationContext()).executor.execute(() -> {
                        saveToInternal(remoteAttachment, localUri, attachment);
                        syncAttachment(remoteRoot, localIterator);
                    }
                )
            );
        } catch (Exception e2) {
            e2.printStackTrace();
            syncAttachment(remoteRoot, localIterator);
        }
    }

    private void saveToInternal(StorageReference remote, Uri localUri, Attachment attachment) {
        try {
            final File file = new File(
                new File(context.getFilesDir(), "attachments"),
                remote.getPath()
            );
            file.getParentFile().mkdirs();
            Files.copy(
                context.getContentResolver().openInputStream(localUri),
                file.toPath()
            );
            updateAttachmentUrl(remote, attachment, file);
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }

    private InputStream uploadStream(InputStream fileInputStream, Uri localUri, String ext) throws IOException {
        if ("jpg".equals(ext) || "jpeg".equals(ext)) {
            final PipedInputStream inputStream = new PipedInputStream();
            final PipedOutputStream outputStream = new PipedOutputStream(inputStream);
            ((JotApplication) context.getApplicationContext()).executor.execute(() -> {
                try {
                    BitmapFactory.decodeStream(
                        fileInputStream
                    ).compress(JPEG, 80, outputStream);
                    outputStream.close();
                } catch (Exception ep) {
                    ep.printStackTrace();
                }
            });
            return inputStream;
        } else {
            return context.getContentResolver().openInputStream(localUri);
        }
    }

    private void updateAttachmentUrl(StorageReference ref, Attachment attachment, File localFile) {
        ref.getDownloadUrl().addOnSuccessListener(uri ->
            new QueriedAttachments(new ByUrl(db, attachment.uri())).value().forEach(res ->
                new UpdateAttachment(db, new WrappedAttachment(res) {
                    @Override
                    public String uri() {
                        return getUriForFile(
                            context,
                            "com.larryhsiao.nyx.fileprovider",
                            localFile
                        ).toString();
                    }
                }).fire()
            )
        );
    }
}
