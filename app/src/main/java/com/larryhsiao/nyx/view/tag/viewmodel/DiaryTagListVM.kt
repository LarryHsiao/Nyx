package com.larryhsiao.nyx.view.tag.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.tag.AttachedTags
import com.larryhsiao.nyx.tag.Tag
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * ViewModel for exist diary tags.
 */
class DiaryTagListVM(app: Application) : AndroidViewModel(app) {
    private val db = RDatabase.Singleton(app).value()
    private val tags = ArrayList<Tag>()
    private val tagsLiveData = MutableLiveData<List<Tag>>().apply {
        value = tags
    }

    /**
     * Tags of the diary have
     */
    fun tags(): LiveData<List<Tag>> {
        return tagsLiveData
    }

    /**
     * Load the tags with given diary id
     */
    fun load(diaryId: Long) {
        GlobalScope.launch {
            tags.clear()
            tags.addAll(AttachedTags(db, diaryId).value())
            tagsLiveData.postValue(tags)
        }
    }
}