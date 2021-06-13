package com.larryhsiao.nyx.core.tags;

import java.util.List;

/**
 * Tags in Nyx.
 */
public interface Tags {
    /**
     * All of tags.
     */
    List<Tag> all();

    List<Tag> byJotId(long id);

    /**
     * Create new {@link Tag} new tag.
     *
     * @return The tag we just create with new Id.
     */
    Tag create(Tag tag);

    Tag create(String name);

    /**
     * Update the given tag.
     */
    void update(Tag tag);

    /**
     * Delete tag by given id.
     */
    void deleteById(long id);
}
