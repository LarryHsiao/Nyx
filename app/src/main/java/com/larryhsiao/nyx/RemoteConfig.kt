package com.larryhsiao.nyx

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

/**
 * Implemenation of [Config]
 */
class RemoteConfig(private val remote: FirebaseRemoteConfig) : Config {

}