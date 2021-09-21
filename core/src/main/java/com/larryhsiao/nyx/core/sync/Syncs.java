package com.larryhsiao.nyx.core.sync;

public interface Syncs {
    public enum Dest {
        DROPBOX
    }

    Dest[] loggedInDest();

    void authCodeFlow(Dest dest, Runnable success);

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
