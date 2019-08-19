package com.larryhsiao.nyx.config

import java.io.File

/**
 * The Config of Nyx, contains properties like media root path or other parameters.
 */
interface Config {
    /**
     * The media root file.
     */
    fun mediaRoot(): File

    /**
     * Backup root file
     */
    fun backupRoot():File

    /**
     * Determine if the bio auth is enabled.
     */
    fun bioAuthEnabled():Boolean
}