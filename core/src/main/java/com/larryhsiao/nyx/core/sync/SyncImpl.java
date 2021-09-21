package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.sync.dropbox.DropboxRemoteFiles;

import java.util.HashMap;
import java.util.Map;

public class SyncImpl implements Syncs {
    private final Map<Dest, String> tokens = new HashMap<>();
    private final Action dbAuthFlow;
    private final Nyx nyx;

    public SyncImpl(Nyx nyx, Action dbAuthFlow) {
        this.nyx = nyx;
        this.dbAuthFlow = dbAuthFlow;
    }

    @Override
    public Dest[] loggedInDest() {
        return tokens.keySet().toArray(new Dest[0]);
    }

    @Override
    public void authCodeFlow(Dest dest, Runnable success) {
        switch (dest){
            // @todo #0 Auth code flow for others
            case DROPBOX:{
                dbAuthFlow.fire();
                break;
            }
        }
    }

    @Override
    public void logout(Dest dest) {
        tokens.remove(dest);
        // @todo #0 Invoke token
    }

    @Override
    public void logoutAll() {
        tokens.clear();
        // @todo #0 Invoke tokens
    }

    @Override
    public void sync() {
        for (Dest dest : tokens.keySet()) {
            switch (dest) {
                // @todo #0 More destinations.
                case DROPBOX: {
                    final RemoteFiles remoteFiles =
                        new DropboxRemoteFiles(tokens.get(Dest.DROPBOX));
                    new SyncAction(
                        nyx,
                        new RemoteIndexes(nyx, remoteFiles),
                        remoteFiles
                    ).fire();
                }
            }
        }
    }
}
