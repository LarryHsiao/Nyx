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

    /**
     * Create an attachment entry with specific id.
     */
    void newAttachmentWithId(Attachment attachment);

    Attachment byId(long id);

    List<Attachment> byJotId(long id);

    /**
     * Replace an attachment without increase version.
     */
    void replace(Attachment attachment);

    void update(Attachment attachment);

    void deleteById(long id);
}
