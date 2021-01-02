package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.observable.Observable;
import com.larryhsiao.clotho.observable.ObservableImpl;
import com.larryhsiao.clotho.serialization.GsonJson;
import com.larryhsiao.clotho.serialization.Json;
import com.larryhsiao.nyx.core.sync.network.*;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of {@link NyxServer}.
 */
public class NyxServerImpl implements NyxServer {
    private final static String PREFIX_DEVICE = "Nyx device===";
    private final static String PREFIX_REQUEST_PAIR = "Nyx request pair===";
    private final ExecutorService sendingExecutor = Executors.newFixedThreadPool(1);
    private final int port;
    private final Json json;
    private final Pinging pinging;
    private final UdpReceiving udpReceiving;
    private final Map<InetAddress, NyxRemote> remoteMap = new HashMap<>();
    private final Observable<Map<InetAddress, NyxRemote>> remotes = new ObservableImpl<>(remoteMap);
    private boolean running = false;

    public NyxServerImpl() {
        this(new GsonJson(), 5555, 5000, 1024);
    }

    public NyxServerImpl(
        Json json,
        int port,
        int interval,
        int bufferSize
    ) {
        this.json = json;
        this.port = port;
        this.pinging = new Pinging(interval, port);
        this.udpReceiving = new UdpReceiving(port, bufferSize, this::onReceived);
    }

    private Void onReceived(Packet incoming) {
        if ("ping".equals(incoming.message())) {
            sendingExecutor.submit(
                () -> new UdpSending(
                    buildPingResponse(incoming),
                    false
                ).fire()
            );
        } else if (incoming.message().startsWith(PREFIX_DEVICE)) {
            remoteMap.put(
                incoming.address(),
                new PingedRemote(
                    json.deserialize(
                        incoming.message().replaceFirst(PREFIX_DEVICE, ""),
                        PingResponse.class
                    )
                )
            );
            System.out.println("=====");
            remoteMap.forEach((inetAddress, nyxRemote) -> {
                System.out.println(
                    inetAddress.getHostName() + " " +
                        nyxRemote.name() + " paired:  " +
                        nyxRemote.isPaired()
                );
            });
            System.out.println("-----");
        } else if (incoming.message().startsWith(PREFIX_REQUEST_PAIR)) {
        }
        return null;
    }

    private Packet buildPingResponse(Packet from) {
        return new Packet(
            PREFIX_DEVICE + json.serialize(
                new PingResponse("Nyx"),
                PingResponse.class
            ),
            from.address(),
            port
        );
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
    public Observable<Map<InetAddress,NyxRemote>> remotes() {
        return remotes;
    }

    @Override
    public void shutdown() {
        running = false;
        pinging.shutdown();
        udpReceiving.shutdown();
    }
}
