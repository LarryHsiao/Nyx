package com.larryhsiao.nyx.diary


/**
 * Phantom object of [Diary]
 */
class PhantomDiary : Diary {
    override fun id(): Long {
        return -1
    }

    override fun title(): String {
        return ""
    }

    override fun timestamp(): Long {
        return -1
    }
}