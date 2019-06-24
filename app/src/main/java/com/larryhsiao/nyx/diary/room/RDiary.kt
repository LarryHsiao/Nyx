package com.larryhsiao.nyx.diary.room

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The room entry for diary. Prefix "R" for Room.
 */
@Entity(tableName = "diary")
data class RDiary(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val title: String?,
    val timestamp: Long?
)