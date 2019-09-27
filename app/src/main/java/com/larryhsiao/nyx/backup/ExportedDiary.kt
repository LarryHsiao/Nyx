package com.larryhsiao.nyx.backup

/**
 * Object of exported diary
 */
interface ExportedDiary {
    /**
     * The json of diary
     */
    fun json(): String
}