package com.larryhsiao.nyx.core.jots

/**
 * Wrap object of Jot
 */
open class WrappedJot(private val jot: Jot) : Jot {
    override fun id(): Long {
        return jot.id()
    }

    override fun title(): String {
        return jot.title()
    }

    override fun content(): String {
        return jot.content()
    }

    override fun createdTime(): Long {
        return jot.createdTime()
    }

    override fun mood(): String {
        return jot.mood()
    }

    override fun location(): DoubleArray {
        return jot.location()
    }

    override fun version(): Int {
        return jot.version()
    }

    override fun deleted(): Boolean {
        return jot.deleted()
    }

    override fun privateLock(): Boolean {
        return jot.privateLock()
    }
}