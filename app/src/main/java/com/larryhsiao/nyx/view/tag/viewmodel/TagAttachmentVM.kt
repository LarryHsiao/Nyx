package com.larryhsiao.nyx.view.tag.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.tag.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * View model for tag title input.
 * This ViewModel will find/create tags by title.
 */
class TagAttachmentVM(app: Application) : AndroidViewModel(app) {
    private val db = RDatabase.Singleton(app).value()
    private val tempTags = HashMap<String, Tag>()

    /**
     * The Tag map for prefered Tags.
     */
    fun tempTags():Map<String, Tag>{
        return tempTags
    }

    /**
     * Setup the initial tags
     */
    fun load(tags: List<Tag>) {
        tags.forEach { tempTags[it.title()] = it }
    }

    /**
     * Record the given tag name.
     */
    fun preferTag(tagName: String) {
        GlobalScope.launch {
            tempTags[tagName] = TagSource(db, tagName).value()
        }
    }

    /**
     * Remove tag from map by tagName/title
     *
     * Note: The tag created by [preferTag] will remains in database.
     * This method just remove the record in memory.
     */
    fun removeTag(tagName: String) {
        tempTags.remove(tagName)
    }

    /**
     * Attach the recorded tags to diary with given dairyId.
     *
     * @Return Tag list attached to diary.
     */
    fun attachToDiary(diaryId: Long): LiveData<List<Tag>> {
        val liveData = MutableLiveData<List<Tag>>()
        GlobalScope.launch {
            DettachAllTagByDiary(db, diaryId).fire()
            db.tagDiaryDao().create(
                diaryId,
                tempTags.values.mapTo(ArrayList(), { it.id() })
            )
            liveData.postValue(AttachedTags(db, diaryId).value())
        }
        return liveData
    }
}