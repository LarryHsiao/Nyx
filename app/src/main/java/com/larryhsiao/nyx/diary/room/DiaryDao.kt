package com.larryhsiao.nyx.diary.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE


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
    fun byTimestamp(start: Long, end:Long): List<RDiary>

    /**
     * Create a diary entry
     */
    @Insert(onConflict = REPLACE)
    fun create(diaryEntity: DiaryEntity): Long

    /**
     * Delete given diary.
     */
    @Delete
    fun delete(diaryEntity: DiaryEntity)
}