package com.larryhsiao.nyx.old;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

/**
 * Implementation remote config
 */
public class NyxRemoteConfigImpl implements NyxRemoteConfig {
    private final FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();

    @Override
    public boolean premiumEnabled() {
        return config.getBoolean("premium_enabled");
    }
}
