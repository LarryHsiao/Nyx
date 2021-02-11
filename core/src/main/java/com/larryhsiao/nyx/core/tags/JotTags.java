package com.larryhsiao.nyx.core.tags;

import java.util.List;

/**
 * The jot tags.
 */
public interface JotTags {
    /**
     * All the Jot tag Link.
     */
    List<JotTag> all();

    /**
     * Create a link from tag to jot.
     */
    void link(long jotId, long tagId);

    /**
     * Update a exist jotTag.
     */
    void update(JotTag jotTag);

    /**
     * Delete a jot tag link by jot and tag id.
     */
    void deleteByIds(long jotId, long tagId);
}
