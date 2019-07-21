package com.larryhsiao.nyx.youtube

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * View model for Youtube video searching.
 */
class YoutubeSearchViewModel(
    private val context: Application
) : AndroidViewModel(context) {
    private val data = MutableLiveData<List<Video>>()

    /**
     * Method to access the search result .
     */
    fun data(): LiveData<List<Video>> {
        return data
    }

    /**
     * Search youtube
     */
    fun search(keyword: String) {
        GlobalScope.launch {
            data.postValue(
                YoutubeVideoSearching(
                    context,
                    keyword
                ).value()
            )
        }
    }
}