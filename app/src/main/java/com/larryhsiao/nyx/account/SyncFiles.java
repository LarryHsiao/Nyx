package com.larryhsiao.nyx.account;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
import java.nio.file.Files;
import java.sql.Connection;
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
        MimeTypeMap map = MimeTypeMap.getSingleton();
        for (final Attachment attachment : attachmentMap.values()) {
            if (!attachment.uri().startsWith(URI_PATH)) {
                upload(map, attachment, remoteStorage);
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
    }

    private void upload(MimeTypeMap map, Attachment attachment, StorageReference reference) {
        final String ext = map.getExtensionFromMimeType(
            new UriMimeType(context, attachment.uri()).value()
        );
        Uri localUri = Uri.parse(attachment.uri());
        StorageReference remote = reference.child(
            UUID.randomUUID().toString() + "." + (ext == null ? "" : ext)
        );
        remote.getMetadata().addOnSuccessListener(meta -> {
            // @todo #1 Handle if file changed, deleted.
            System.out.println(meta.getName() + " " + meta.getContentType());
        }).addOnFailureListener(e -> {
            try {
                remote.putStream(
                    context.getContentResolver().openInputStream(localUri)
                ).addOnSuccessListener(it -> {
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
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        });
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
