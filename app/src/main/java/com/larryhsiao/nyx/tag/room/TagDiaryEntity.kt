package com.larryhsiao.nyx.tag.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Releation table for Tag and Diary.
 */
@Entity(
    tableName = "tag_diary",
    indices = [Index(
        value = ["diary_id", "tag_id"],
        unique = true
    )]
)
data class TagDiaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "diary_id") val diaryId: Long,
    @ColumnInfo(name = "tag_id") val tagId: Long
)