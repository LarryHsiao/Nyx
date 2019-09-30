package com.larryhsiao.nyx.backup.tag

import com.google.gson.Gson

/**
 * Object Iterator to json.
 */
class ObjectIterator(private val iterator: Iterator<Any>) : Iterator<String> {
    private val gson = Gson()
    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    override fun next(): String {
        if (hasNext().not()) {
            throw NoSuchElementException()
        }
        return gson.toJson(iterator.next())
    }
}