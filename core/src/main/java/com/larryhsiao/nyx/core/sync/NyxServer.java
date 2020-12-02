package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.observable.Observable;
import com.larryhsiao.clotho.observable.ObservableImpl;

import java.util.List;

/**
 * A Nyx server for doing device authorization, searching and syncing.
 */
public interface NyxServer {
    /**
     * Fire up this server.
     */
    void launch();

    /**
     * @return The detected Nyx devices.
     */
    Observable<List<NyxRemote>> remotes();

    /**
     * Shutdown the server.
     */
    void shutdown();
}
