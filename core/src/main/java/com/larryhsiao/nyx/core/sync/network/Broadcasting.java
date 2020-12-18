package com.larryhsiao.nyx.core.sync.network;

import com.larryhsiao.clotho.Action;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Action to send a broadcast message
 */
public class Broadcasting implements Action {
    private final String broadcastMessage;
    private final InetAddress address;
    private final int port;

    public Broadcasting(
        String broadcastMessage,
        InetAddress address,
        int port
    ) {
        this.broadcastMessage = broadcastMessage;
        this.address = address;
        this.port = port;
    }

    @Override
    public void fire() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            byte[] buffer = broadcastMessage.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
