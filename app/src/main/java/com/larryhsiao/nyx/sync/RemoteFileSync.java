package com.larryhsiao.nyx.sync;

import android.content.Context;
import android.net.Uri;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.larryhsiao.nyx.core.attachments.AllAttachments;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.attachments.QueriedAttachments;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.larryhsiao.nyx.JotApplication.URI_FILE_PROVIDER;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

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
    private final String keyStr;
    private SecretKeySpec key;

    public RemoteFileSync(
        Context context, Source<Connection> db, String uid, String keyStr) {
        this.context = context;
        this.db = db;
        this.uid = uid;
        this.keyStr = keyStr;
    }

    @Override
    public void fire() {
        try {
            initKey();
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initKey() throws NoSuchAlgorithmException {
        key = new SecretKeySpec(
            Arrays.copyOf(
                MessageDigest.getInstance("SHA-1").digest(keyStr.getBytes(UTF_8)),
                16
            ), "AES"
        );
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
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(ENCRYPT_MODE, key);
            // @todo #0 Handle upload failed.
            Tasks.await(remoteRoot.child(localFile.getName()).putStream(
                new CipherInputStream(new FileInputStream(localFile), cipher))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void download(StorageReference remoteRoot, File dist) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(DECRYPT_MODE, key);
            StreamDownloadTask task = remoteRoot.child(dist.getName()).getStream(
                (taskSnapshot, inputStream) -> {
                    Files.copy(
                        new CipherInputStream(inputStream, cipher),
                        dist.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    );
                    inputStream.close();
                }
            );
            Tasks.await(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
