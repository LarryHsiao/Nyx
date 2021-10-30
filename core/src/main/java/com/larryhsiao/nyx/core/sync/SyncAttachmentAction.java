package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.clotho.file.UriFileName;
import com.larryhsiao.clotho.io.ProgressedCopy;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.attachments.Attachment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Action to sync attachments to remote.
 */
public class SyncAttachmentAction implements Action {
    private final Nyx nyx;
    private final RemoteFiles remoteFiles;
    private final NyxIndexes remoteIndexes;
    private final ExecutorService worker = Executors.newFixedThreadPool(
       2  // @todo #100 Find best thread pool size
    );

    public SyncAttachmentAction(Nyx nyx, RemoteFiles remoteFiles, NyxIndexes remoteIndexes) {
        this.nyx = nyx;
        this.remoteFiles = remoteFiles;
        this.remoteIndexes = remoteIndexes;
    }

    @Override
    public void fire() {
        final List<Attachment> remoteUpdates = new ArrayList<>();
        final Map<Long, Attachment> localUpdates = nyx.attachments()
            .all()
            .stream()
            .collect(Collectors.toMap(Attachment::id, Function.identity()));
        final Map<Long, Attachment> remoteAttachments = remoteIndexes.attachments()
            .stream()
            .collect(Collectors.toMap(Attachment::id, Function.identity()));
        final List<Future<?>> asyncFuture = new ArrayList<>();
        for (Attachment remoteAttachment : remoteAttachments.values()) {
            if (localUpdates.containsKey(remoteAttachment.id())) {
                final Attachment localAttachment = localUpdates.get(remoteAttachment.id());
                if (remoteAttachment.version() > localAttachment.version()) {
                    asyncFuture.add(worker.submit(() -> {
                        try {
                            nyx.attachments().replace(remoteAttachment);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }));
                } else if (remoteAttachment.version() < localAttachment.version()) {
                    remoteUpdates.add(localAttachment);
                }
                localUpdates.remove(remoteAttachment.id());
            } else {
                asyncFuture.add(worker.submit(() -> {
                    updateLocaleFile(remoteAttachment);
                }));
            }
        }
        for (Future<?> future : asyncFuture) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        remoteUpdates.addAll(localUpdates.values());
        updateRemoteFiles(remoteUpdates);
    }

    private void updateLocaleFile(Attachment remoteAttachment) {
        try {
            nyx.attachments().newAttachmentWithId(remoteAttachment);
            final File dstFile = nyx.files().fileByUri(remoteAttachment.uri());
            if (remoteAttachment.deleted()) {
                dstFile.delete();
            } else {
                dstFile.getParentFile().mkdirs();
                downloadAttachmentFile(remoteAttachment, dstFile);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void downloadAttachmentFile(Attachment remoteAttachment, File dstFile) {
        try {
            final String fileName = new UriFileName(remoteAttachment.uri()).value();
            new ProgressedCopy(
                remoteFiles.get(
                    String.format(
                        "/%s/%s",
                        remoteAttachment.jotId() + "",
                        fileName
                    )
                ),
                new FileOutputStream(dstFile),
                4096,
                true,
                integer -> null
            ).fire();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateRemoteFiles(List<Attachment> remoteUpdates) {
        try {
            final List<Future<?>> asyncFuture = new ArrayList<>();
            for (Attachment remoteUpdate : remoteUpdates) {
                final File sourceFile = nyx.files().fileByUri(remoteUpdate.uri());
                final String fileName = new UriFileName(remoteUpdate.uri()).value();
                final String remoteFilePath = String.format(
                    "/%s/%s",
                    remoteUpdate.jotId() + "",
                    fileName
                );
                if (remoteUpdate.deleted()) {
                    asyncFuture.add(
                        worker.submit(() -> {
                            try {
                                remoteFiles.delete(remoteFilePath);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
                    );
                } else {
                    if (!sourceFile.exists()) {
                        continue;
                    }
                    asyncFuture.add(
                        worker.submit(() -> {
                            try {
                                remoteFiles.post(remoteFilePath, new FileInputStream(sourceFile));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
                    );
                }
            }
            for (Future<?> future : asyncFuture) {
                future.get();
            }
            remoteIndexes.updateAttachments(remoteUpdates);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
