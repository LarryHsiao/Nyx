package com.larryhsiao.nyx.jot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larryhsiao.nyx.core.jots.*
import com.silverhetch.clotho.Source
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import kotlin.Double.Companion.MIN_VALUE

/**
 * ViewModel to representing a jot.
 */
class JotViewModel(private val db: Source<Connection>) : ViewModel() {
    private val time = MutableLiveData<Long>()
    fun time(): LiveData<Long> = time

    private val content = MutableLiveData<String>()
    fun content(): LiveData<String> = content

    private val title = MutableLiveData<String>()
    fun title(): LiveData<String> = title

    private val location = MutableLiveData<DoubleArray>()
    fun location(): LiveData<DoubleArray> = location

    private val jot = MutableLiveData<Jot>()

    private val isNewJotLiveData = MutableLiveData<Boolean>()
    fun isNewJot(): LiveData<Boolean> = isNewJotLiveData

    fun loadJot(id: Long) = viewModelScope.launch(IO) {
        if (id != -1L) {
            isNewJotLiveData.postValue(false)
            loadContent(JotById(id, db).value())
        } else {
            isNewJotLiveData.postValue(true)
            loadContent(ConstJot())
        }
    }

    private fun loadContent(newJot: Jot) = viewModelScope.launch {
        jot.value = newJot
        time.value = newJot.createdTime()
        content.value = newJot.content()
        title.value = newJot.title()
        location.value = newJot.location()
    }

    suspend fun save(): Jot = withContext(IO) {
        PostedJot(db, object : WrappedJot(jot.value ?: ConstJot()) {
            override fun content(): String = content.value ?: ""
            override fun title(): String = title.value ?: ""
            override fun createdTime(): Long = time.value ?: 0L
            override fun location(): DoubleArray = location.value?: doubleArrayOf(MIN_VALUE, MIN_VALUE)
        }).value()
    }

    fun preferTitle(newTitle: String) {
        if (title.value == newTitle) {
            return
        }
        title.value = newTitle
    }

    fun preferContent(newContent: String) {
        if (content.value == newContent) {
            return
        }
        content.value = newContent
    }

    fun preferTime(newTime: Long) {
        time.value = newTime
    }

    fun preferLocation(newLocation: DoubleArray){
        location.value = newLocation
    }
}