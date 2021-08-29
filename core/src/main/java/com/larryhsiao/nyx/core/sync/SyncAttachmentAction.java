package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.attachments.Attachments;

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
        // @todo #200
        final Attachments attachments = nyx.attachments();
    }
}
