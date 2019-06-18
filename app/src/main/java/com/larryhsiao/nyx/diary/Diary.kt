package com.larryhsiao.nyx.diary

/**
 * Represents a diary.
 */
interface Diary {
    /**
     * Id of this diary
     */
    fun id(): Long

    /**
     * The main title
     */
    fun title(): String

    /**
     * The record timestamp
     */
    fun timestamp(): Long
}