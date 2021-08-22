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
        new SyncTagsAction(nyx, remoteIndexes).fire();
        new SyncJotsAction(nyx, remoteFiles, remoteIndexes).fire();
    }
}
