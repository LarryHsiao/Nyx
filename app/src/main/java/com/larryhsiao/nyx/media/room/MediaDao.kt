package com.larryhsiao.nyx.media.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

/**
 * The media Dao
 */
@Dao
interface MediaDao {
    /**
     * Create media entity.
     */
    @Insert(onConflict = REPLACE)
    fun create(entity: MediaEntity): Long

    /**
     * Create media entities.
     */
    @Insert(onConflict = REPLACE)
    fun create(entity: List<MediaEntity>): List<Long>

    /**
     * Delete media row by given media id.
     */
    @Query("DELETE FROM media WHERE id=:id")
    fun delete(id: Long)

    /**
     * Delete media rows by given diary id.
     */
    @Query("DELETE FROM media WHERE diary_id=:diaryId")
    fun deleteByDiaryId(diaryId: Long)

    /**
     * Query Medias by diary id.
     */
    @Query("SELECT * FROM media WHERE diary_id=:diaryId;")
    fun byDiaryId(diaryId: Long): List<MediaEntity>

    /**
     * Query all medias
     */
    @Query("SELECT * FROM media;")
    fun all(): List<MediaEntity>

    /**
     * Remove all rows
     */
    @Query("DELETE FROM media;")
    fun clear()
}