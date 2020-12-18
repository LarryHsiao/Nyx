package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.observable.Observable;
import com.larryhsiao.clotho.observable.ObservableImpl;
import com.larryhsiao.nyx.core.sync.network.Packet;
import com.larryhsiao.nyx.core.sync.network.Pinging;
import com.larryhsiao.nyx.core.sync.network.UdpReceiving;
import com.larryhsiao.nyx.core.sync.network.UdpSending;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of {@link NyxServer}.
 */
public class NyxServerImpl implements NyxServer {
    private static final int port = 5555;
    private static final int interval = 5000; // 5 sec
    private final ExecutorService sendingExecutor = Executors.newFixedThreadPool(1);
    private final Pinging pinging = new Pinging(interval, port);
    private final UdpReceiving udpReceiving = new UdpReceiving(port, 1024, this::onReceived);
    private boolean running = false;
    private final Observable<List<NyxRemote>> remotes = new ObservableImpl<>(
        new ArrayList<>()
    );

    private Void onReceived(Packet packet) {
        send(packet);
        System.out.println(packet.message());
        return null;
    }

    @Override
    public void launch() {
        running = true;
        pinging.launch();
        udpReceiving.launch();
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
        udpReceiving.shutdown();
    }

    public void send(Packet packet) {
        sendingExecutor.submit(() -> new UdpSending(
            packet,
            false
        ).fire());
    }
}
