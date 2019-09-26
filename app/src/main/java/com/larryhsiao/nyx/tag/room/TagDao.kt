package com.larryhsiao.nyx.tag.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

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
     * Query tag by id
     */
    @Query("SELECT * FROM tag WHERE id=:id")
    fun byId(id: Long): TagEntity

    /**
     * All tags which attached on diary that matched the given id.
     */
    @Query("SELECT * FROM tag_diary JOIN tag on tag_diary.tag_id=tag.id WHERE diary_id=:id")
    fun byDiaryId(id: Long): List<TagEntity>

    /**
     * Query tag by title
     */
    @Query("SELECT * FROM tag WHERE title=:title")
    fun byName(title: String): TagEntity?

    /**
     * Search Tag by given keyword
     *
     * @param keyword the keyword we wonder to search, if the keyword might have
     * character before and after itself, should pass %keyword% to this parameter.
     */
    @Query("SELECT * FROM tag WHERE title LIKE :keyword")
    fun searchByName(keyword: String): List<TagEntity>
}
