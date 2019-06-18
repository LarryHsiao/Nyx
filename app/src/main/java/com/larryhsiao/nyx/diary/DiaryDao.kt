package com.larryhsiao.nyx.diary

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE


/**
 * Room dao required by Room, We don`t use this class directly. Wrap it with objects.
 */
@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary")
    fun all(): List<RDiary>

    @Insert(onConflict = REPLACE)
    fun create(diary: RDiary): Long

    @Delete
    fun delete(diary: RDiary)
}