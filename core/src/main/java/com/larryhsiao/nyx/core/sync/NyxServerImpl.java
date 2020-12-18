package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.observable.Observable;
import com.larryhsiao.clotho.observable.ObservableImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link NyxServer}.
 */
public class NyxServerImpl implements NyxServer {
    private static final int port = 5555;
    private static final int interval = 5000; // 5 sec
    private final Pinging pinging = new Pinging(interval, port);
    private final Server server = new Server(port, 1024);
    private boolean running = false;
    private final Observable<List<NyxRemote>> remotes = new ObservableImpl<>(
        new ArrayList<>()
    );

    @Override
    public void launch() {
        running = true;
        pinging.launch();
        server.launch();
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public Observable<List<NyxRemote>> remotes() {
        return remotes;
    }

    @Override
    public void shutdown() {
        running = false;
        pinging.shutdown();
        server.shutdown();
    }
}
