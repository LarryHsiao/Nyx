package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.nyx.core.Nyx;

/**
 * Action for sync local Nyx to remote file-base system.
 */
public class SyncAction implements Action {
    private final Nyx nyx;
    private final NyxIndexes remoteIndexes;
    private final RemoteFiles remoteFiles;

    public SyncAction(
        Nyx nyx,
        NyxIndexes remoteIndexes,
        RemoteFiles remoteFiles
    ) {
        this.nyx = nyx;
        this.remoteIndexes = remoteIndexes;
        this.remoteFiles = remoteFiles;
    }

    @Override
    public void fire() {
        if (remoteIndexes.isLocked()) {
            // @todo #109 Invalidate lock file
            return;
        }
        remoteIndexes.lock();
        new SyncTagsAction(nyx.tags(), remoteIndexes).fire();
        new SyncJotsAction(nyx, remoteFiles, remoteIndexes).fire();
        new SyncAttachmentAction(nyx, remoteFiles, remoteIndexes).fire();
        new SyncMetadataAction(nyx.metadataSet(), remoteIndexes, remoteFiles).fire();
        remoteIndexes.unlock();
    }
}
