package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.observable.Observable;
import com.larryhsiao.clotho.observable.ObservableImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link NyxServer}.
 */
public class NyxServerImpl implements NyxServer {
    private boolean running = false;
    private final Observable<List<NyxRemote>> remotes = new ObservableImpl<>(
        new ArrayList<>()
    );

    @Override
    public void launch() {
        running = true;
        new Thread(() -> {
            while (running) {
                for (InetAddress address : new AllBroadcastAddresses().value()) {
                    new NetworkBroadcasting("abc", address,4445).fire();
                }
                try {
                    Thread.sleep(5000);
                } catch (Exception ignore) {
                }
            }
        }).start();

        new Thread(() -> {
            try {
                byte[] buf = new byte[256];
                DatagramSocket socket = new DatagramSocket(4445);
                boolean running = true;
                while (running) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    packet = new DatagramPacket(buf, buf.length, address, port);
                    String received = new String(
                        packet.getData(),
                        0,
                        packet.getLength()
                    );
                    System.out.println("received: " + received);
                    if (received.equals("end")) {
                        running = false;
                        continue;
                    }
                    socket.send(packet);
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public Observable<List<NyxRemote>> remotes() {
        return remotes;
    }

    @Override
    public void shutdown() {
        running = false;
    }
}
