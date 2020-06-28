package com.larryhsiao.nyx.sync;

import android.content.Context;
import android.net.Uri;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.larryhsiao.nyx.core.attachments.AllAttachments;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.attachments.QueriedAttachments;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
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
        for (Attachment attachment : attachmentMap.values()) {
            syncAttachment(FirebaseStorage.getInstance().getReference(uid), attachment);
        }
    }

    private void syncAttachment(StorageReference remoteRoot, Attachment attachment) {
        if (attachment.uri().startsWith(URI_FILE_PROVIDER)) {
            syncFile(remoteRoot, attachment);
        }
    }

    private void syncFile(StorageReference remoteRoot, Attachment attachment) {
        final File localFile = new File(new File(
            context.getFilesDir(),
            "attachments"
        ), attachment.uri().replace(URI_FILE_PROVIDER, ""));
        final StorageReference ref = remoteRoot.child(localFile.getName());
        Task<Uri> task = ref.getDownloadUrl();
        try {
            Tasks.await(task);
            if (task.isSuccessful()) {
                if (attachment.deleted()) {
                    Tasks.await(ref.delete());
                } else {
                    if (!localFile.exists()) {
                        localFile.getParentFile().mkdirs();
                        download(remoteRoot, localFile);
                    }
                }
            } else {
                if (localFile.exists() && !attachment.deleted()) {
                    upload(remoteRoot, localFile);
                }
                // @todo #0 Handle missing file.
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (localFile.exists() && !attachment.deleted()) {
                upload(remoteRoot, localFile);
            }
        }
    }

    private void upload(StorageReference remoteRoot, File localFile) {
        try {
            // @todo #0 Handle upload failed.
            Tasks.await(remoteRoot.child(localFile.getName()).putStream(
                new FileInputStream(localFile)
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void download(StorageReference remoteRoot, File dist) {
        try {
            FileDownloadTask task = remoteRoot.child(dist.getName()).getFile(dist);
            Tasks.await(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
