package com.larryhsiao.nyx.jot

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larryhsiao.clotho.openweather.JsonWeather
import com.larryhsiao.clotho.openweather.OpenWeatherSource
import com.larryhsiao.clotho.openweather.Weather
import com.larryhsiao.nyx.BuildConfig.OPEN_WEATHER_API_KEY
import com.larryhsiao.nyx.core.StringHashTags
import com.larryhsiao.nyx.core.attachments.*
import com.larryhsiao.nyx.core.jots.*
import com.larryhsiao.nyx.core.metadata.*
import com.larryhsiao.nyx.core.metadata.Metadata.Type.OPEN_WEATHER
import com.larryhsiao.nyx.core.metadata.openweather.PostedWeatherMeta
import com.larryhsiao.nyx.core.metadata.openweather.WeatherRemovalByJotId
import com.larryhsiao.nyx.core.tags.*
import com.silverhetch.clotho.Action
import com.silverhetch.clotho.Source
import com.silverhetch.clotho.source.ConstSource
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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
    companion object {
        private const val TAG_DETECTION_DELAY_SEC = 1
    }

    private var secondsAfterContentChanged = Int.MAX_VALUE

    init {
        viewModelScope.launch(IO) {
            while (isActive) {
                if (secondsAfterContentChanged == TAG_DETECTION_DELAY_SEC) {
                    preferTags()
                }
                secondsAfterContentChanged++
                delay(1000)
            }
        }
    }

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

    private val weather = MutableLiveData<Weather>()
    fun weather(): LiveData<Weather> = weather

    private val tags: MutableMap<String, Tag> = HashMap<String, Tag>()
    private val tagsLiveData = MutableLiveData<Map<String, Tag>>().apply {
        value = tags
    }

    private val privateLock = MutableLiveData<Boolean>()
    fun privateLock(): LiveData<Boolean> = privateLock

    fun tags(): LiveData<Map<String, Tag>> = tagsLiveData

    fun loadJot(id: Long) = viewModelScope.launch(IO) {
        if (id == -1L) {
            newJot(Calendar.getInstance())
        } else {
            isNewJotLiveData.postValue(false)
            val jot = JotById(id, db).value()
            loadContent(jot)
            loadAttachments(jot)
            loadWeather(jot)
            loadTags(jot)
        }
    }

    private fun loadTags(jot: Jot) {
        tagsLiveData.postValue(
            QueriedTags(
                TagsByJotId(db, jot.id())
            ).value()
                .map { it.title() to it }
                .toMap()
        )
    }

    private fun loadWeather(jot: Jot) {
        val filter = QueriedMetadata(
            MetadataByJotId(db, jot.id())
        ).value().filter { it.type() == OPEN_WEATHER }

        if (filter.isNotEmpty()) {
            weather.postValue(JsonWeather(
                filter[0].value()
            ))
        }
    }

    fun newJot(date: Calendar) = viewModelScope.launch(IO) {
        isNewJotLiveData.postValue(true)
        loadContent(object : WrappedJot(ConstJot()) {
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
        privateLock.value = newJot.privateLock()
    }

    private fun loadAttachments(jot: Jot) = viewModelScope.launch(IO) {
        attachments.postValue(
            QueriedAttachments(AttachmentsByJotId(db, jot.id())).value().map { it.uri() }
        )
    }

    suspend fun save() = withContext(IO) {
        preferTags()
        val savedJot = PostedJot(db, object : WrappedJot(jot.value ?: ConstJot()) {
            override fun content() = content.value ?: ""
            override fun title() = title.value ?: ""
            override fun createdTime() = time.value ?: 0L
            override fun location() = location.value ?: doubleArrayOf(MIN_VALUE, MIN_VALUE)
            override fun privateLock() = privateLock.value ?: false
        }).value()
        saveAttachments(savedJot)
        saveWeather(savedJot)
        saveTags(savedJot)
    }

    private fun saveTags(jot: Jot) {
        JotTagsRemoval(db, jot.id()).fire()
        tags.values.forEach { selected ->
            val tag = CreatedTagByName(db, selected.title()).value()
            NewJotTag(
                db,
                ConstSource(jot.id()),
                ConstSource(tag.id())
            ).fire()
        }
    }

    private fun saveWeather(savedJot: Jot) {
        if (weather.value == null) {
            WeatherRemovalByJotId(db, savedJot.id()).fire()
        } else {
            weather.value?.let {
                PostedWeatherMeta(db, it, savedJot.id()).value()
            }
        }
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
        secondsAfterContentChanged = 0
        markModified()
    }

    private fun preferTags() {
        val newTagsMap = StringHashTags(
            content.value ?: ""
        ).value().map {
            // In-memory tag object, we actually create tag when we saving it.
            it to ConstTag(-1, it)
        }.toMap()
        if (newTagsMap.keys.toTypedArray().contentEquals(tags.keys.toTypedArray())) {
            return
        }
        tags.filter { newTagsMap.containsKey(it.key).not() }.forEach { deleted -> tags.remove(deleted.key) }
        tags.putAll(newTagsMap)
        tagsLiveData.postValue(tags)
    }

    fun preferTime(newTime: Long) {
        time.value = newTime
        updateWeather()
        markModified()
    }

    fun preferLocation(newLocation: DoubleArray) {
        location.value = newLocation
        updateWeather()
        markModified()
    }

    fun togglePrivateContent() {
        privateLock.value = !(privateLock.value ?: false)
        markModified()
    }

    private fun updateWeather() {
        val selectLocation = location.value
        if (selectLocation != null && weather.value == null &&
            !selectLocation.contentEquals(doubleArrayOf(MIN_VALUE, MIN_VALUE)) &&
            dayCalendar(time.value ?: System.currentTimeMillis()).timeInMillis ==
            dayCalendar(System.currentTimeMillis()).timeInMillis) {
            loadWeather(selectLocation[0], selectLocation[1])
        } else {
            weather.value = null
        }
    }

    private fun loadWeather(longitude: Double, latitude: Double) = viewModelScope.launch {
        weather.value = withContext(IO) {
            OpenWeatherSource(
                OPEN_WEATHER_API_KEY,
                latitude,
                longitude
            ).value()
        }
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

    private fun dayCalendar(time: Long): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
}