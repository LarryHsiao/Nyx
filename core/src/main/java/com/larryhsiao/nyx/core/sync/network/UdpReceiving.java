package com.larryhsiao.nyx.core.sync.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.function.Function;

/**
 * The actual server for handling data receiving,
 */
public class UdpReceiving {
    private final int port;
    private final int bufferSize;
    private final Function<Packet, Void> onReceived;
    private boolean running = false;

    public UdpReceiving(int port, int bufferSize, Function<Packet, Void> onReceived) {
        this.port = port;
        this.bufferSize = bufferSize;
        this.onReceived = onReceived;
    }

    public boolean isRunning() {
        return running;
    }

    public void launch() {
        if (running) {
            return;
        }
        running = true;
        new Thread(() -> {
            try {
                byte[] buf = new byte[bufferSize];
                final DatagramSocket socket = new DatagramSocket(port);
                final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                while (running) {
                    socket.receive(packet);
                    onReceived.apply(new Packet(
                        new String(packet.getData(), packet.getOffset(), packet.getLength()),
                        packet.getAddress(),
                        packet.getPort()
                    ));
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Shutdown the server by set the frag to false to shutdown at next loop.
     */
    public void shutdown() {
        running = false;
    }
}
