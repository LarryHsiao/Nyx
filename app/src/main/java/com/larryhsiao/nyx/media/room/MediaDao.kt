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
     * Delete media row by given media id.
     */
    @Query("DELETE FROM media WHERE id=:id")
    fun delete(id: Long)
}