package com.larryhsiao.nyx.sync;

import android.content.Context;
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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.larryhsiao.nyx.JotApplication.URI_FILE_PROVIDER;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toMap;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

/**
 * Action to sync files to Firebase storage.
 */
public class RemoteFileSync implements Action {
    private final Context context;
    private final Source<Connection> db;
    private final String uid;
    private final String keyStr;
    private final Progress progress;
    private SecretKeySpec key;

    public interface Progress {
        void onProgress(int total, int progress);
    }

    private enum SYNC_ACTION {
        NONE,
        UPLOAD,
        DOWNLOAD,
        DELETE
    }

    public RemoteFileSync(
        Context context,
        Source<Connection> db,
        String uid,
        String keyStr,
        Progress progress
    ) {
        this.context = context;
        this.db = db;
        this.uid = uid;
        this.keyStr = keyStr;
        this.progress = progress;
    }

    @Override
    public void fire() {
        try {
            initKey();
            Map<String, Attachment> attachmentMap = new QueriedAttachments(
                new AllAttachments(db, true)
            ).value().stream().collect(toMap(
                Attachment::uri,
                Function.identity(),
                (attachment, attachment2) -> attachment
            ));
            StorageReference remoteRoot = FirebaseStorage.getInstance().getReference(uid);
            Map<String, StorageReference> remoteItems = Tasks.await(remoteRoot.listAll())
                .getItems()
                .stream()
                .collect(toMap(StorageReference::getName, it -> it));
            Map<String, Attachment> dbItems = attachmentMap.values().stream()
                .filter(it -> it.uri().startsWith(URI_FILE_PROVIDER))
                .collect(toMap(
                    it -> it.uri().replace(URI_FILE_PROVIDER, ""),
                    it -> it));
            Map<File, StorageReference> upload = new HashMap<>();
            Map<File, StorageReference> download = new HashMap<>();
            Map<File, StorageReference> delete = new HashMap<>();
            for (Map.Entry<String, Attachment> entry : dbItems.entrySet()) {
                final File localFile = new File(new File(
                    context.getFilesDir(),
                    "attachments"
                ), entry.getKey());
                final StorageReference remoteFileRef = remoteItems.get(localFile.getName());
                switch (syncFile(localFile, entry, remoteFileRef)) {
                    case UPLOAD:
                        upload.put(localFile, remoteRoot.child(entry.getKey()));
                        break;
                    case DOWNLOAD:
                        download.put(localFile, remoteFileRef);
                        break;
                    case DELETE:
                        delete.put(localFile, remoteFileRef);
                        break;
                }
            }
            int count = 0;
            final int total = upload.size() + download.size() + delete.size();
            for (File file : upload.keySet()) {
                upload(upload.get(file), file);
                progress.onProgress(total, ++count);
            }
            for (File file : download.keySet()) {
                download(download.get(file), file);
                progress.onProgress(total, ++count);
            }
            for (File file : delete.keySet()) {
                Tasks.await(delete.get(file).delete());
                progress.onProgress(total, ++count);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initKey() throws NoSuchAlgorithmException {
        key = new SecretKeySpec(Arrays.copyOf(
            MessageDigest.getInstance("SHA-1").digest(keyStr.getBytes(UTF_8)),
            16
        ), "AES");
    }

    private SYNC_ACTION syncFile(
        File localFile,
        Map.Entry<String, Attachment> entry,
        StorageReference remoteFileRef
    ) {
        try {
            if (remoteFileRef == null) {
                if (localFile.exists() && !entry.getValue().deleted()) {
                    return SYNC_ACTION.UPLOAD;
                }
                // Missing if the file is still uploading from original phone
            } else {
                if (entry.getValue().deleted()) {
                    return SYNC_ACTION.DELETE;
                } else {
                    if (!localFile.exists()) {
                        localFile.getParentFile().mkdirs();
                        return SYNC_ACTION.DOWNLOAD;
                    }
                }
            }
            return SYNC_ACTION.NONE;
        } catch (Exception e) {
            e.printStackTrace();
            if (localFile.exists() && !entry.getValue().deleted()) {
                return SYNC_ACTION.UPLOAD;
            }
            return SYNC_ACTION.NONE;
        }
    }

    private void upload(StorageReference remoteFileRef, File localFile) throws Exception{
        if (localFile.length() > 1024 * 1024 * 20) { // limit to 20 MB, @todo #1 Config object for limiting attachment file size.
            return;
        }
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(ENCRYPT_MODE, key);
        // @todo #0 Handle upload failed.
        Tasks.await(remoteFileRef.putStream(
            new CipherInputStream(new FileInputStream(localFile), cipher))
        );
    }

    private void download(StorageReference remoteRoot, File dist) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(DECRYPT_MODE, key);
        StreamDownloadTask task = remoteRoot.getStream((taskSnapshot, inputStream) -> {
                Files.copy(
                    new CipherInputStream(inputStream, cipher),
                    dist.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                );
                inputStream.close();
            }
        );
        Tasks.await(task);
    }
}
