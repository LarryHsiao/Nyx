package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.attachments.Attachments;
import com.larryhsiao.nyx.core.tags.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Action to sync attachments to remote.
 */
public class SyncAttachmentAction implements Action {
    private final Nyx nyx;
    private final RemoteFiles remoteFiles;
    private final NyxIndexes remoteIndexes;

    public SyncAttachmentAction(Nyx nyx, RemoteFiles remoteFiles, NyxIndexes remoteIndexes) {
        this.nyx = nyx;
        this.remoteFiles = remoteFiles;
        this.remoteIndexes = remoteIndexes;
    }

    @Override
    public void fire() {
        final List<Attachment> remoteUpdates = new ArrayList<>();
        final Map<Long, Attachment> localMap = nyx.attachments()
            .all()
            .stream()
            .collect(Collectors.toMap(Attachment::id, Function.identity()));
        final Map<Long, Attachment> remoteAttachments = remoteIndexes.attachments()
            .stream()
            .collect(Collectors.toMap(Attachment::id, Function.identity()));
        for (Attachment remoteAttachment : remoteAttachments.values()) {
            if (localMap.containsKey(remoteAttachment.id())) {
                final Attachment localAttachment = localMap.get(remoteAttachment.id());
                if (remoteAttachment.version() > localAttachment.version()) {
                    nyx.attachments().replace(remoteAttachment);
                } else if (remoteAttachment.version() < localAttachment.version()) {
                    remoteUpdates.add(localAttachment);
                }
                // Remove updated tag so new tag at local remains.
                localMap.remove(remoteAttachment.id());
            } else {
                nyx.attachments().newAttachmentWithId(remoteAttachment);
            }
        }
        remoteUpdates.addAll(localMap.values());
        remoteIndexes.updateAttachments(remoteUpdates);
    }
}
