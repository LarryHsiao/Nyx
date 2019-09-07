package com.larryhsiao.nyx.view.tag.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.tag.AllTags
import com.larryhsiao.nyx.tag.Tag
import com.larryhsiao.nyx.tag.TagSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * View model to present a tag list
 */
class TagListVM(app: Application) : AndroidViewModel(app) {
    private val db = RDatabase.Singleton(app).value()
    private val tags = ArrayList<Tag>()
    private val tagLive = MutableLiveData<List<Tag>>().apply { value = tags }

    /**
     * Load the tags
     */
    fun loadUpTags() {
        GlobalScope.launch {
            tags.clear()
            tags.addAll(AllTags(db).value())
            tagLive.postValue(tags)
        }
    }

    /**
     * The tags list live data.
     */
    fun tags(): LiveData<List<Tag>> {
        return tagLive
    }

    /**
     * Create tag by input title
     */
    fun createTag(title: String): LiveData<Tag> {
        val live = MutableLiveData<Tag>()
        GlobalScope.launch {
            if (db.tagDao().byName(title) == null) {
                live.postValue(
                    TagSource(
                        db,
                        title
                    ).value().apply { tags.add(this) })
            }
        }
        return live
    }
}