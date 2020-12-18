package com.larryhsiao.nyx.core.sync.network;

import java.net.InetAddress;

public class Packet {
    private final String message;
    private final InetAddress address;
    private final int port;

    Packet(String message, InetAddress address, int port) {
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