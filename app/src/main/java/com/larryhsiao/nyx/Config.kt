package com.larryhsiao.nyx

import java.io.File

/**
 * The Config of Nyx, contains properties like media root path or other parameters.
 */
interface Config {
    /**
     * The media root file.
     */
    fun mediaRoot(): File
}