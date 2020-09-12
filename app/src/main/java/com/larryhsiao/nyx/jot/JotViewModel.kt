package com.larryhsiao.nyx.jot

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larryhsiao.nyx.core.attachments.*
import com.larryhsiao.nyx.core.jots.*
import com.silverhetch.clotho.Action
import com.silverhetch.clotho.Source
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.util.*
import kotlin.Double.Companion.MIN_VALUE
import kotlin.collections.ArrayList

/**
 * ViewModel to representing a jot.
 */
class JotViewModel(
    private val db: Source<Connection>,
    private val localFileSync: Action
) : ViewModel() {
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

    private val attachments = MutableLiveData<List<String>>()
    fun attachments(): LiveData<List<String>> = attachments

    private val isModified = MutableLiveData<Boolean>()
    fun isModified(): LiveData<Boolean> = isModified

    fun loadJot(id: Long) = viewModelScope.launch(IO) {
        if (id == -1L) {
            newJot(Calendar.getInstance())
        } else {
            isNewJotLiveData.postValue(false)
            val jot = JotById(id, db).value()
            loadContent(jot)
            loadAttachments(jot)
        }
    }

    fun newJot(date:Calendar) = viewModelScope.launch(IO){
        isNewJotLiveData.postValue(true)
        loadContent(object:WrappedJot(ConstJot()){
            override fun createdTime(): Long {
                return date.timeInMillis
            }
        })
    }

    private fun loadContent(newJot: Jot) = viewModelScope.launch {
        jot.value = newJot
        time.value = newJot.createdTime()
        content.value = newJot.content()
        title.value = newJot.title()
        location.value = newJot.location()
    }

    private fun loadAttachments(jot: Jot) = viewModelScope.launch(IO) {
        attachments.postValue(
            QueriedAttachments(AttachmentsByJotId(db, jot.id())).value().map { it.uri() }
        )
    }

    suspend fun save() = withContext(IO) {
        val value = PostedJot(db, object : WrappedJot(jot.value ?: ConstJot()) {
            override fun content(): String = content.value ?: ""
            override fun title(): String = title.value ?: ""
            override fun createdTime(): Long = time.value ?: 0L
            override fun location(): DoubleArray = location.value
                ?: doubleArrayOf(MIN_VALUE, MIN_VALUE)
        }).value()
        saveAttachments(value)
    }

    private fun saveAttachments(jot: Jot) {
        val exists = QueriedAttachments(AttachmentsByJotId(db, jot.id())).value()
        val new = (attachments.value ?: emptyList()).map { it }.toHashSet()
        val delete = ArrayList<Attachment>()
        exists.forEach { exist ->
            if (!new.contains(exist.uri())) {
                delete.add(exist)
            }
            new.remove(exist.uri())
        }
        new.forEach { NewAttachment(db, it, jot.id()).value() }
        delete.forEach { RemovalAttachment(db, it.id()).fire() }
        localFileSync.fire()
    }

    fun preferTitle(newTitle: String) {
        if (title.value == newTitle) {
            return
        }
        title.value = newTitle
        markModified()
    }

    fun preferContent(newContent: String) {
        if (content.value == newContent) {
            return
        }
        content.value = newContent
        markModified()
    }

    fun preferTime(newTime: Long) {
        time.value = newTime
        markModified()
    }

    fun preferLocation(newLocation: DoubleArray) {
        location.value = newLocation
        markModified()
    }

    fun preferAttachments(newAttachments: List<Uri>) {
        attachments.value = newAttachments.map { it.toString() }
        markModified()
    }

    suspend fun delete() = withContext(IO) {
        RemovalAttachmentByJotId(db, jot.value?.id() ?: -1).fire()
        JotRemoval(db, jot.value?.id() ?: -1).fire()
    }

    private fun markModified() {
        if (isModified.value != true) {
            isModified.value = true
        }
    }
}