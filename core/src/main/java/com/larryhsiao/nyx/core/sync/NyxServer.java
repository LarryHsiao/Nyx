package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.observable.Observable;
import com.larryhsiao.clotho.observable.ObservableImpl;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

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
    Observable<Map<InetAddress,NyxRemote>> remotes();

    /**
     * Shutdown the server.
     */
    void shutdown();

    boolean isRunning();
}
