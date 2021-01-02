package com.larryhsiao.nyx.core.sync.network;

import java.net.InetAddress;

/**
 * A packet for sending/receiving.
 */
public class Packet {
    private final String message;
    private final InetAddress address;
    private final int port;

    public Packet(String message, InetAddress address, int port) {
        this.message = message;
        this.address = address;
        this.port = port;
    }

    public String message() {
        return message;
    }

    public InetAddress address() {
        return address;
    }

    public int port() {
        return port;
    }
}