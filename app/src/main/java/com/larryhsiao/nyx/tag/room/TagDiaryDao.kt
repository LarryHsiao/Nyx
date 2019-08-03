package com.larryhsiao.nyx.tag.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface TagDiaryDao {
    /**
     * Create relation for given diary and tag.
     */
    @Insert(onConflict = REPLACE)
    fun create(entity: TagDiaryEntity)

    @Query("DELETE FROM tag_diary WHERE diary_id=:id")
    fun deleteByDiaryId(id: Long)

    @Query("DELETE FROM tag_diary WHERE tag_id=:id")
    fun deleteByTagId(id: Long)
}