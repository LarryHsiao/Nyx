package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.sync.dropbox.DropboxRemoteFiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class SyncImpl implements Syncs {
    public interface Login {
        void login(Function<String, Void> callback);
    }
    private final Map<Dest, String> tokens = new HashMap<>();
    private final Login dbAuthFlow;
    private final Nyx nyx;

    public SyncImpl(Nyx nyx, Login dbAuthFlow) {
        this.nyx = nyx;
        this.dbAuthFlow = dbAuthFlow;
    }

    @Override
    public Set<Dest> loggedInDest() {
        return tokens.keySet();
    }

    @Override
    public void login(Dest dest, Runnable success) {
        if (dest == Dest.DROPBOX) {
            dbAuthFlow.login((token) -> {
                tokens.put(Dest.DROPBOX, token);
                return null;
            });
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
            if (dest == Dest.DROPBOX) {
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
