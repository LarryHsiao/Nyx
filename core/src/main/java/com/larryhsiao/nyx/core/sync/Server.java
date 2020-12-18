package com.larryhsiao.nyx.core.sync;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * The actual server for handling data receiving,
 */
class Server {
    private final int port;
    private final int bufferSize;
    private boolean running = false;

    Server(int port, int bufferSize) {
        this.port = port;
        this.bufferSize = bufferSize;
    }

    public boolean isRunning() {
        return running;
    }

    public void launch() {
        running = true;
        new Thread(() -> {
            try {
                byte[] buf = new byte[bufferSize];
                DatagramSocket socket = new DatagramSocket(port);
                while (running) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    InetAddress address = packet.getAddress();
                    packet = new DatagramPacket(buf, buf.length, address, packet.getPort());
                    String received = new String(
                        packet.getData(),
                        0,
                        packet.getLength()
                    );
                    System.out.println("received: " + received);
                    socket.send(packet);
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
