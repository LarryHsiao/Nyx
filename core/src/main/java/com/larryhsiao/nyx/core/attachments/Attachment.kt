package com.larryhsiao.nyx.core.attachments

/**
 * Attachment of a Jot
 */
interface Attachment {
    /**
     * Id of this attachment.
     */
    fun id(): Long

    /**
     * The Jot id that this attachment file attached to.
     */
    fun jotId(): Long

    /**
     * Uri of this attachment.
     */
    fun uri(): String

    /**
     * Version of this attachment item.
     */
    fun version(): Int

    /**
     * Indicates if this attachment is deleted or not.
     */
    fun deleted(): Boolean
}