package com.larryhsiao.nyx.sync;

import android.content.Context;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.larryhsiao.nyx.core.attachments.AllAttachments;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.attachments.QueriedAttachments;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.larryhsiao.nyx.JotApplication.URI_FILE_PROVIDER;

/**
 * Action to sync files to Firebase storage.
 *
 * @todo #2 syncing progress indicator.
 * @todo #3 File size limitation.
 * @todo #4 Query multiple remote files a time.
 */
public class RemoteFileSync implements Action {
    private final Context context;
    private final Source<Connection> db;
    private final String uid;

    public RemoteFileSync(Context context, Source<Connection> db, String uid) {
        this.context = context;
        this.db = db;
        this.uid = uid;
    }

    @Override
    public void fire() {
        Map<String, Attachment> attachmentMap = new QueriedAttachments(
            new AllAttachments(db, true)
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

    private void syncAttachment(
        StorageReference remoteRoot,
        Iterator<Attachment> iterator
    ) {
        if (!iterator.hasNext()) {
            return;
        }
        final Attachment attachment = iterator.next();
        if (!attachment.uri().startsWith(URI_FILE_PROVIDER)) {
            syncAttachment(remoteRoot, iterator); // Next
        } else {
            syncFile(remoteRoot, attachment, iterator);
        }
    }

    private void syncFile(
        StorageReference remoteRoot,
        Attachment attachment,
        Iterator<Attachment> iterator
    ) {
        final File localFile = new File(new File(
            context.getFilesDir(),
            "attachments"
        ), attachment.uri().replace(URI_FILE_PROVIDER, ""));
        final StorageReference ref = remoteRoot.child(localFile.getName());
        ref.getMetadata().addOnSuccessListener(it -> {
            if (attachment.deleted()) {
                ref.delete().addOnCompleteListener(it2->{
                   syncAttachment(remoteRoot, iterator);
                });
            } else {
                if (localFile.exists()) {
                    syncAttachment(remoteRoot, iterator); // exist
                } else {
                    localFile.getParentFile().mkdirs();
                    download(remoteRoot, localFile, iterator);
                }
            }
        }).addOnFailureListener(it -> {
            if (localFile.exists()&& !attachment.deleted()) {
                upload(remoteRoot, localFile, iterator);
            } else {
                syncAttachment(remoteRoot, iterator);
                // @todo #0 Handle missing file.
            }
        });
    }

    private void upload(
        StorageReference remoteRoot,
        File localFile,
        Iterator<Attachment> iterator
    ) {

        try {
            remoteRoot.child(localFile.getName()).putStream(
                new FileInputStream(localFile)
            ).addOnSuccessListener(it -> {
                syncAttachment(remoteRoot, iterator);
            }).addOnFailureListener(it -> {
                // @todo #0 Handle upload failed.
                syncAttachment(remoteRoot, iterator);
            });
        } catch (IOException e) {
            e.printStackTrace();
            // @todo #0 Handle open file download failed.
            syncAttachment(remoteRoot, iterator);
        }
    }

    private void download(
        StorageReference remoteRoot,
        File dist,
        Iterator<Attachment> iterator
    ) {
        remoteRoot.child(dist.getName())
            .getFile(dist)
            .addOnSuccessListener(taskSnapshot ->
                syncAttachment(remoteRoot, iterator)
            ).addOnFailureListener(e ->
            // @todo #0 handle download faild or survey if there are any side effect if we ignore it.
            syncAttachment(remoteRoot, iterator)
        );
    }
}
