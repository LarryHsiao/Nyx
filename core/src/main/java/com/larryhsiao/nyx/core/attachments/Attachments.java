package com.larryhsiao.nyx.core.attachments;

import java.util.List;

public interface Attachments {
    /**
     * All of the attachments.
     */
    List<Attachment> all();

    /**
     * Method to create a attachment.
     * @return The attachment we just created with new Id.
     */
    Attachment newAttachment(Attachment attachment);

    Attachment byId(long id);
}
