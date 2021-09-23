package com.larryhsiao.nyx.core.sync;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public interface Syncs {
    public enum Dest {
        DROPBOX
    }

    Set<Dest> loggedInDest();

    void login(Dest dest, Runnable success);

    /**
     * Logout given destination
     */
    void logout(Dest dest);

    void logoutAll();

    /**
     * Sync all the destination logged in.
     */
    void sync();
}
