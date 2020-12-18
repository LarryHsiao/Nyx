package com.larryhsiao.nyx.core.sync.network;

import com.larryhsiao.clotho.Action;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Action to send a broadcast message
 */
public class UdpSending implements Action {
    private final Packet packet;
    private final boolean broadcast;

    public UdpSending(Packet packet, boolean broadcast) {
        this.packet = packet;
        this.broadcast = broadcast;
    }

    @Override
    public void fire() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(broadcast);
            byte[] buffer = packet.message().getBytes();
            DatagramPacket jdkPacket = new DatagramPacket(
                buffer,
                buffer.length,
                packet.address(),
                packet.port()
            );
            socket.send(jdkPacket);
            socket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
