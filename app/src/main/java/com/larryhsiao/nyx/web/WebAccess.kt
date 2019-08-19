package com.larryhsiao.nyx.web

/**
 * Access Jotted via web.
 */
interface WebAccess {
    /**
     * Enable the web access
     */
    fun enable()

    /**
     * Disable the web access
     */
    fun disable()

    /**
     * For determining if the access is running.
     */
    fun isRunning():Boolean
}