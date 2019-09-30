package com.larryhsiao.nyx.tag.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction

/**
 * Dao for tag_diary relation table.
 */
@Dao
interface TagDiaryDao {
    /**
     * Create relation for given diary and tag.
     */
    @Insert(onConflict = REPLACE)
    fun create(entity: TagDiaryEntity): Long

    /**
     * Create multiple tag relation in one transaction.
     */
    @Transaction
    fun create(diaryId: Long, tagIds: Collection<Long>) {
        tagIds.forEach {
            create(
                TagDiaryEntity(
                    0,
                    diaryId,
                    it
                )
            )
        }
    }

    /**
     * Delete all relations by diary Id
     */
    @Query("DELETE FROM tag_diary WHERE diary_id=:id")
    fun deleteByDiaryId(id: Long)

    /**
     * Delete all relations by tag id
     */
    @Query("DELETE FROM tag_diary WHERE tag_id=:id")
    fun deleteByTagId(id: Long)

    /**
     * Delete a entity by ids.
     */
    @Query("DELETE FROM tag_diary WHERE tag_id=:tagId AND diary_id=:diaryId")
    fun delete(diaryId: Long, tagId: Long)

    /**
     * Query all tag diary.
     */
    @Query("SELECT * FROM tag_diary;")
    fun all(): List<TagDiaryEntity>
}