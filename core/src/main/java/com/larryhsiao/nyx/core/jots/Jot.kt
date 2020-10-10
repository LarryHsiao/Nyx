package com.larryhsiao.nyx.core.jots

/**
 * A note
 */
interface Jot {
    /**
     * id of a jot.
     */
    fun id(): Long

    /**
     * The title of this jot.
     */
    fun title(): String

    /**
     * The content of a jot.
     */
    fun content(): String

    /**
     * Created time
     */
    fun createdTime(): Long

    /**
     * The display location of this Jot
     *
     * @return length = 2, longitude and latitude.
     */
    fun location(): DoubleArray

    /**
     * A mood emoji for this jot.
     */
    fun mood(): String

    /**
     * Version of this Jot. For syncing to replace from.
     */
    fun version(): Int

    /**
     * Indicates this is a private content.
     */
    fun privateLock(): Boolean

    /**
     * Determine this Jot is deleted.
     */
    fun deleted(): Boolean
}