package com.larryhsiao.nyx.core.attachments

/**
 * Wrapped class of attachment
 */
open class WrappedAttachment(private val origin: Attachment) : Attachment {
    override fun id(): Long {
        return origin.id()
    }

    override fun jotId(): Long {
        return origin.jotId()
    }

    override fun uri(): String {
        return origin.uri()
    }

    override fun version(): Int {
        return origin.version()
    }

    override fun deleted(): Boolean {
        return origin.deleted()
    }
}