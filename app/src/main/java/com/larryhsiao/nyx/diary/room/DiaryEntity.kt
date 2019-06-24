package com.larryhsiao.nyx.diary.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * The room entry for diary table.
 */
@Entity(tableName = "diary")
data class DiaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val title: String?,
    val timestamp: Long?
)