package com.larryhsiao.nyx.media

import android.content.Context
import com.larryhsiao.nyx.ConfigImpl
import com.larryhsiao.nyx.media.room.MediaDao
import com.larryhsiao.nyx.media.room.MediaEntity
import com.silverhetch.clotho.Source

/**
 * Create multiple media from given uris.
 * Actions:
 * - Move given file to internal Storage
 * - Record new file uri to database.
 */
class NewMedias(
    private val context: Context,
    private val dao: MediaDao,
    private val diaryId: Long,
    private val uris: List<String>
) : Source<List<MediaEntity>> {
    override fun value(): List<MediaEntity> {
        val result = ArrayList<MediaEntity>()
        uris.forEach {
            result.add(
                NewMedia(
                    context,
                    dao,
                    diaryId,
                    ConfigImpl(context).mediaRoot(),
                    it
                ).value()
            )
        }
        return result
    }
}