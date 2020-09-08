package com.larryhsiao.nyx.core.attachments

/**
 * Constant of Attachment
 */
class ConstAttachment(
    private val id: Long,
    private val jotId: Long,
    private val uri: String,
    private val version: Int,
    private val delete: Int
) : Attachment {
    override fun jotId(): Long {
        return jotId
    }

    override fun uri(): String {
        return uri
    }

    override fun id(): Long {
        return id
    }

    override fun version(): Int {
        return version
    }

    override fun deleted(): Boolean {
        return delete == 1
    }
}