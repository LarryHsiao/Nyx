package com.larryhsiao.nyx.diary

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
    @Query("SELECT * FROM diary")
    fun all(): List<RDiary>

    /**
     * Select diaries by timestamp rang
     */
    @Query("SELECT * FROM diary WHERE timestamp>=:start AND timestamp<:end")
    fun byTimestamp(start: Long, end:Long): List<RDiary>

    /**
     * Create a diary entry
     */
    @Insert(onConflict = REPLACE)
    fun create(diary: RDiary): Long

    /**
     * Delete given diary.
     */
    @Delete
    fun delete(diary: RDiary)
}