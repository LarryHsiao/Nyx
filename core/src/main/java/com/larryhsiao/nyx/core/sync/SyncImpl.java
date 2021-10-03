package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.storage.Ceres;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.sync.dropbox.DropboxRemoteFiles;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class SyncImpl implements Syncs {
    public interface Login {
        void login(Function<String, Void> callback);
    }

    private final Source<Map<Dest, String>> tokenSrc;
    private final Login dbAuthFlow;
    private final Nyx nyx;
    private final Ceres ceres;
    private boolean isSyncing = false;

    public SyncImpl(Nyx nyx, Login dbAuthFlow, Ceres ceres, Source<Map<Dest, String>> tokenSrc) {
        this.nyx = nyx;
        this.dbAuthFlow = dbAuthFlow;
        this.ceres = ceres;
        this.tokenSrc = tokenSrc;
    }

    @Override
    public Set<Dest> loggedInDest() {
        return tokenSrc.value().keySet();
    }

    @Override
    public Map<Dest, Jwt> loggedInAccount() {
        final Map<Dest, Jwt> account = new HashMap<>();
        tokenSrc.value().forEach((dest, s) -> {
            try {
                account.put(
                    dest,
                    new JsonJwt(dest, Json.createReader(new StringReader(s)).readObject())
                );
            } catch (Exception e) {
                e.printStackTrace();
                // @todo #130 Publish the failure.
            }
        });
        return account;
    }

    @Override
    public void login(Dest dest, Runnable success) {
        if (dest == Dest.DROPBOX) {
            dbAuthFlow.login((token) -> {
                tokenSrc.value().put(Dest.DROPBOX, token);
                ceres.store(Dest.DROPBOX.name(), token);
                success.run();
                return null;
            });
        }
    }

    @Override
    public void logout(Dest dest) {
        tokenSrc.value().remove(dest);
        ceres.store(Dest.DROPBOX.name(), "");
        // @todo #0 Invoke token
    }

    @Override
    public void logoutAll() {
        tokenSrc.value().clear();
        for (Dest value : Dest.values()) {
            ceres.delete(value.name());
        }
        // @todo #0 Invoke tokens
    }

    @Override
    public void sync() {
        try {
            if (isSyncing) {
                return;
            }
            isSyncing = true;
            for (Dest dest : tokenSrc.value().keySet()) {
                if (dest == Dest.DROPBOX) {
                    final String jsonString = tokenSrc.value().get(Dest.DROPBOX);
                    if (jsonString.length() == 0) {
                        continue;
                    }
                    final JsonObject jsonObj = Json.createReader(
                        new StringReader(jsonString)
                    ).readObject();
                    final RemoteFiles remoteFiles = new DropboxRemoteFiles(
                        jsonObj.getString("access_token")
                    );
                    new SyncAction(
                        nyx,
                        new RemoteIndexes(nyx, remoteFiles),
                        remoteFiles
                    ).fire();
                }
            }
        } finally {
            isSyncing = false;
        }
    }
}
