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
    private final MimeTypeMap map = MimeTypeMap.getSingleton();
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
        StorageReference remoteStorage = FirebaseStorage.getInstance().getReference(uid);
        for (final Attachment attachment : attachmentMap.values()) {
            syncAttachment(attachment, remoteStorage);
        }
    }

    private void syncAttachment(Attachment attachment, StorageReference remoteStorage) {
        if (!attachment.uri().startsWith(URI_PATH)) {
            checkRemoteExist(map, attachment, remoteStorage);
        } else {
            final String fileName = attachment.uri().replace(URI_PATH, "");
            final File localFile = new File(new File(
                context.getFilesDir(),
                "attachments"
            ), fileName);
            localFile.getParentFile().mkdirs();
            if (!localFile.exists()) {
                remoteStorage.child(fileName.replace(uid + "/", ""))
                    .getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        System.out.println("Download success");
                    }).addOnFailureListener(e -> {
                    System.out.println("Download Failed " + e);
                });
            }
        }
    }

    private void checkRemoteExist(MimeTypeMap map, Attachment attachment, StorageReference ref) {
        final String ext = map.getExtensionFromMimeType(
            new UriMimeType(context, attachment.uri()).value()
        );
        Uri localUri = Uri.parse(attachment.uri());
        StorageReference remote = ref.child(
            UUID.randomUUID().toString() + "." + (ext == null ? "" : ext)
        );
        remote.getMetadata().addOnSuccessListener(meta -> {
            // @todo #1 Handle if file changed, deleted.
            System.out.println(meta.getName() + " " + meta.getContentType());
        }).addOnFailureListener(e -> {
            try {
                upload(remote, localUri, ext, attachment);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        });
    }

    private void upload(
        StorageReference remote,
        Uri localUri, String ext,
        Attachment attachment
    ) throws IOException {
        remote.putStream(uploadStream(localUri, ext)).addOnSuccessListener(it -> {
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
        });
    }

    private InputStream uploadStream(Uri localUri, String ext) throws IOException {
        if ("jpg".equals(ext) || "jpeg".equals(ext)) {
            final PipedInputStream inputStream = new PipedInputStream();
            final PipedOutputStream outputStream = new PipedOutputStream(inputStream);
            ((JotApplication) context.getApplicationContext()).executor.execute(() -> {
                try {
                    BitmapFactory.decodeStream(
                        context.getContentResolver().openInputStream(localUri)
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
            new QueriedAttachments(
                new ByUrl(db, attachment.uri())
            ).value().forEach(res ->
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
