package com.larryhsiao.nyx.sync;

import android.content.Context;
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
import com.silverhetch.aura.images.JpegCompress;
import com.silverhetch.aura.uri.UriMimeType;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.file.ToFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            uploadToRemote(mimeType, attachment, remoteRoot, iterator);
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
                    .addOnSuccessListener(taskSnapshot ->
                        syncAttachment(remoteRoot, iterator)
                    ).addOnFailureListener(e ->
                    syncAttachment(remoteRoot, iterator)
                );
            } else {
                syncAttachment(remoteRoot, iterator);
            }
        }
    }

    private void uploadToRemote(
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
            uploadToRemote(map, attachment, remoteRoot, localAttachments); // remote exist, retry
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
        if ("jpg".equals(ext) || "jpeg".equals(ext)) {
            uploadJpg(remoteRoot, remoteAttachment, attachment, localIterator);
        } else {
            upload(remoteRoot, remoteAttachment, attachment, localIterator);
        }
    }

    private void uploadJpg(
        StorageReference remoteRoot,
        StorageReference remoteAttachment,
        Attachment attachment,
        Iterator<Attachment> localIterator
    ) {
        Uri localUri = Uri.parse(attachment.uri());
        ((JotApplication) context.getApplicationContext()).executor.execute(() -> {
            try {
                File origin = Files.createTempFile("ori", ".jpg").toFile();
                File compressedFile = Files.createTempFile("dist", ".jpg").toFile();
                new ToFile(
                    streamFromUri(localUri),
                    origin,
                    integer -> null
                ).fire();
                new JpegCompress(origin, compressedFile).fire();
                origin.delete();
                remoteAttachment.putStream(
                    new FileInputStream(compressedFile)
                ).addOnSuccessListener(it ->
                    ((JotApplication) context.getApplicationContext())
                        .executor.execute(() -> {
                            saveToInternal(remoteAttachment, localUri, attachment);
                            syncAttachment(remoteRoot, localIterator);
                        }
                    )
                ).addOnFailureListener(it ->
                    syncAttachment(remoteRoot, localIterator)
                ).addOnCompleteListener(it -> compressedFile.delete());
            } catch (IOException|SecurityException e) {
                e.printStackTrace();
                syncAttachment(remoteRoot, localIterator);
            }
        });
    }

    private void upload(
        StorageReference remoteRoot,
        StorageReference remoteAttachment,
        Attachment attachment,
        Iterator<Attachment> localIterator

    ) {
        try {
            Uri localUri = Uri.parse(attachment.uri());
            remoteAttachment.putStream(
                streamFromUri(localUri)
            ).addOnSuccessListener(it ->
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
                streamFromUri(localUri),
                file.toPath()
            );
            updateAttachmentUrl(remote, attachment, file);
        } catch (Exception ioException) {
            ioException.printStackTrace();
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

    private InputStream streamFromUri(Uri uri) throws IOException, SecurityException{
        if (uri.toString().startsWith("file:")){
            return new FileInputStream(new File(uri.toString().replaceFirst("file:","")));
        }else{
            return context.getContentResolver().openInputStream(uri);
        }
    }
}
