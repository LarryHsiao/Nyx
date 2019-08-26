package com.larryhsiao.nyx.tag.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

/**
 * Dao of tag
 *
 * @see TagEntity
 */
@Dao
interface TagDao {
    /**
     * Create an entity of tag.
     */
    @Insert()
    fun create(entity: TagEntity): Long

    /**
     * Create na entity of tag if not exist, otherwise return exist one.
     */
    @Transaction
    fun queryOrCreate(title: String): TagEntity {
        val exist = byName(title)
        return if (exist == null) {
            val id = create(TagEntity(0, title))
            TagEntity(id, title)
        } else {
            exist
        }
    }

    /**
     * All of tags
     */
    @Query("SELECT * FROM tag ORDER BY title;")
    fun all(): List<TagEntity>

    /**
     * Delete a tag by id.
     */
    @Query("DELETE FROM tag WHERE id=:id;")
    fun delete(id: Long)

    /**
     * All tags which attached on diary that matched the given id.
     */
    @Query("SELECT * FROM tag LEFT JOIN tag_diary WHERE diary_id=:id")
    fun byDiaryId(id: Long): List<TagEntity>

    @Query("SELECT * FROM tag WHERE title=:title")
    fun byName(title: String): TagEntity?
}
