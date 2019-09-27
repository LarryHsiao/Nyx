package com.larryhsiao.nyx.tag.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tag entity for Room(Jetpack)
 *
 * @todo #tagging-1 Tagging db CRUD　for diaries.
 */
@Entity(
    tableName = "tag",
    indices = [Index(value = ["title"], unique = true)]
)
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val title: String
)