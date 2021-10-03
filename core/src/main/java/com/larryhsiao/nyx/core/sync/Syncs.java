package com.larryhsiao.nyx.core.sync;

import java.util.Map;
import java.util.Set;

public interface Syncs {
    enum Dest {
        DROPBOX
    }

    Set<Dest> loggedInDest();

    Map<Dest, Jwt> loggedInAccount();

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
