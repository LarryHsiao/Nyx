package com.larryhsiao.nyx.core.sync;

public interface Jwt {
    String id();

    String accessToken();

    Syncs.Dest type();

    String accountId();
}
