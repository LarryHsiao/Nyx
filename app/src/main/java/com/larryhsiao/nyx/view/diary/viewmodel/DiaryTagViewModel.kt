package com.larryhsiao.nyx.view.diary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.tag.DbTag
import com.larryhsiao.nyx.tag.Tag
import com.larryhsiao.nyx.tag.AttachedTags
import com.larryhsiao.nyx.tag.DbTagAttachment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

/**
 * ViewModel of tag attache to a diary.
 */
class DiaryTagViewModel(private val app: Application) : AndroidViewModel(app) {
    private val db = RDatabase.Singleton(app).value()
    private val tags = HashMap<String, Tag>()
    private val tagsLive = MutableLiveData<Map<String, Tag>>().apply {
        value = tags
    }
    private var diaryId: Long = 0

    /**
     * Tags of given diary
     */
    fun tags(): LiveData<Map<String, Tag>> {
        return tagsLive
    }

    /**
     * Load up the tags by diary
     */
    fun loadUp(diaryId: Long) {
        tagsLive.value = AttachedTags(db, diaryId)
            .value().associateBy({ it.title() }, { it }).toMutableMap()
    }

    /**
     * Attach a tag to diary, create a entity to db if tag not exist.
     */
    fun newTag(tagName: String): LiveData<Tag> {
        val liveData = MutableLiveData<Tag>()
        GlobalScope.launch {
            if (diaryId == 0L) {
                throw IllegalArgumentException("Should load up diary first by calling loadUp()")
            }
            val tag = db.tagDao().queryOrCreate(tagName)
            db.tagDiaryDao().create(
                tag.id,
                listOf(diaryId)
            )
            liveData.value = DbTagAttachment(
                db,
                DbTag(db, tag),
                diaryId
            ).apply { tags[title()] = this }
        }
        return liveData
    }

    /**
     * Detach ta tag from diary, just break the relation, not delete the tag itself from db.
     */
    fun removeTag(tagName: String) {
        GlobalScope.launch {
            tags.remove(tagName)
        }
    }
}