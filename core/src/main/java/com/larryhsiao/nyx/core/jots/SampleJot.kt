package com.larryhsiao.nyx.core.jots

/**
 * Sample data of Jot
 */
class SampleJot : Jot {
    override fun title(): String {
        return "Sample title"
    }

    override fun id(): Long {
        return -1
    }

    override fun content(): String {
        return "This is sample string of Jot."
    }

    override fun location(): DoubleArray {
        return doubleArrayOf(0.0, 0.0)
    }

    override fun createdTime(): Long {
        return System.currentTimeMillis()
    }

    override fun mood(): String {
        return String(Character.toChars(0x1F600))
    }

    override fun version(): Int {
        return 1
    }

    override fun deleted(): Boolean {
        return false
    }

    override fun privateLock(): Boolean {
        return false
    }
}