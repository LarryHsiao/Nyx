package com.larryhsiao.nyx.core.tags;

import java.util.List;

public interface Tags {
    // @todo #112 Tags

    /**
     * All of tags.
     */
    List<Tag> all();

    /**
     * Create new {@link Tag} new tag.
     *
     * @return The tag we just create with new Id.
     */
    Tag create(Tag tag);

    /**
     * Update the given tag.
     */
    void update(Tag tag);

    /**
     * Delete tag by given id.
     */
    void deleteById(long id);
}
