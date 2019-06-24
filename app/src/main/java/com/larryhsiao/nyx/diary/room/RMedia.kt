package com.larryhsiao.nyx.diary.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media")
data class RMedia(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val meta: String,
    val value: String
)