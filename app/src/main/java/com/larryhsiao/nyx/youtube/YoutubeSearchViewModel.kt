package com.larryhsiao.nyx.youtube

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * View model for Youtube video searching.
 */
class YoutubeSearchViewModel(
    private val context: Application
) : AndroidViewModel(context) {
    private val data = MutableLiveData<List<Video>>()
    private val error = MutableLiveData<Exception>()

    /**
     * Method to access the search result .
     */
    fun data(): LiveData<List<Video>> {
        return data
    }

    /**
     * Method of access error
     */
    fun error(): LiveData<Exception> {
        return error
    }

    /**
     * Search youtube
     */
    fun search(keyword: String) {
        GlobalScope.launch {
            try {
                data.postValue(
                    YoutubeVideoSearching(
                        context,
                        keyword
                    ).value()
                )
            } catch (e: IOException) {
                error.postValue(e)
            }
        }
    }
}