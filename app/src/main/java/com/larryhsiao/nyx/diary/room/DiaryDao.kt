package com.larryhsiao.nyx.diary.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.larryhsiao.nyx.diary.Diary

/**
 * Room dao required by Room, We don`t use this class directly. Wrap it with objects.
 */
@Dao
interface DiaryDao {
    /**
     * Select all the diary in database
     */
    @Query("SELECT * FROM diary  ORDER BY timestamp DESC")
    fun all(): List<RDiary>

    /**
     * Select diaries by timestamp rang
     */
    @Query("SELECT * FROM diary WHERE timestamp>=:start AND timestamp<:end ORDER BY timestamp DESC")
    fun byTimestamp(start: Long, end: Long): List<RDiary>

    /**
     * Select diaries by timestamp rang
     */
    @Query("SELECT * FROM tag_diary JOIN diary ON tag_diary.diary_id=diary.id WHERE tag_diary.tag_id=:id  ORDER BY timestamp DESC")
    fun byTagId(id: Long): List<RDiary>

    /**
     * Create a diary entry
     */
    @Insert(onConflict = REPLACE)
    fun create(diaryEntity: DiaryEntity): Long

    /**
     * Update exist [Diary]
     */
    @Update
    fun update(diary: DiaryEntity)

    /**
     * Query diary by id
     */
    @Query("SELECT * FROM diary WHERE id=:id")
    fun byId(id: Long): RDiary

    /**
     * Delete given diary.
     */
    @Delete
    fun delete(diaryEntity: DiaryEntity)

    /**
     * Delete diary by given id.
     */
    @Query("DELETE FROM diary WHERE id=:id;")
    fun delete(id: Long)

    /**
     * Remove all rows
     */
    @Query("DELETE FROM diary")
    fun clear()
}