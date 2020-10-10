package com.larryhsiao.nyx.core.jots

/**
 * Const of Jot
 */
open class ConstJot : Jot {
    private val id: Long
    private val title: String
    private val content: String
    private val createdTime: Long
    private val location: DoubleArray
    private val mood: String
    private val version: Int
    private val deleted: Boolean
    private val private:Boolean

    @JvmOverloads
    constructor(
        id: Long = -1,
        title: String = "",
        content: String = "",
        createdTime: Long = System.currentTimeMillis(),
        location: DoubleArray = doubleArrayOf(Double.MIN_VALUE, Double.MIN_VALUE),
        mood: String = "",
        version: Int = 1,
        deleted: Boolean = false,
        private: Boolean = false
    ) {
        this.id = id
        this.title = title
        this.content = content
        this.createdTime = createdTime
        this.location = location
        this.mood = mood
        this.version = version
        this.deleted = deleted
        this.private = private
    }

    constructor(jot: Jot) {
        id = jot.id()
        title = jot.title()
        location = jot.location()
        createdTime = jot.createdTime()
        content = jot.content()
        mood = jot.mood()
        version = jot.version()
        deleted = jot.deleted()
        private = jot.privateLock()
    }

    override fun title(): String {
        return title
    }

    override fun id(): Long {
        return id
    }

    override fun content(): String {
        return content
    }

    override fun createdTime(): Long {
        return createdTime
    }

    override fun location(): DoubleArray {
        return location
    }

    override fun mood(): String {
        return mood
    }

    override fun version(): Int {
        return version
    }

    override fun deleted(): Boolean {
        return deleted
    }

    override fun privateLock(): Boolean {
        return private
    }
}