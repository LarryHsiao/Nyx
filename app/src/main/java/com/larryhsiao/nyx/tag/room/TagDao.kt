package com.larryhsiao.nyx.tag.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

/**
 * Dao of tag
 *
 * @see TagEntity
 */
@Dao
abstract class TagDao {
    /**
     * Create an entity of tag.
     */
    @Insert(onConflict = REPLACE)
    abstract fun create(entity: TagEntity)

    /**
     * All of tags
     */
    @Query("SELECT * FROM tag ORDER BY title;")
    abstract fun all(): List<TagEntity>

    /**
     * Delete a tag by id.
     */
    @Query("DELETE FROM tag WHERE id=:id;")
    abstract fun delete(id: Long)

    /**
     * All tags which attached on diary that matched the given id.
     */
    @Query("SELECT * FROM tag LEFT JOIN tag_diary WHERE diary_id=:id")
    abstract fun byDiaryId(id: Long): List<TagEntity>
}
