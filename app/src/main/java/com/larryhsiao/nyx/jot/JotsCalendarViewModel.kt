package com.larryhsiao.nyx.jot

import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larryhsiao.nyx.JotApplication
import com.larryhsiao.nyx.core.attachments.NewAttachments
import com.larryhsiao.nyx.core.jots.*
import com.larryhsiao.nyx.core.jots.goemetry.MeterDelta
import com.larryhsiao.nyx.jot.JotsCalendarViewModel.ListType.LIST
import com.silverhetch.aura.images.exif.ExifAttribute
import com.silverhetch.aura.images.exif.ExifUnixTimeStamp
import com.silverhetch.clotho.source.ConstSource
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date
import java.util.*
import kotlin.Double.Companion.MIN_VALUE
import kotlin.collections.HashMap
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * ViewModel to represent Jot list.
 */
class JotsCalendarViewModel(private val app: JotApplication) : ViewModel() {
    enum class ListType {
        LIST, MAP
    }

    private val selected = MutableLiveData<Calendar>().apply {
        value = Calendar.getInstance()
    }
    private val jots = MutableLiveData<List<Jot>>()
    private val loading = MutableLiveData<Boolean>()
    private val listType = MutableLiveData<ListType>().apply { value = LIST }

    fun loading(): LiveData<Boolean> = loading

    fun selected(): LiveData<Calendar> = selected

    fun jots(): LiveData<List<Jot>> = jots

    fun listType(): LiveData<ListType> = listType

    fun initJots() {
        if (jots.value == null) {
            selectDate(selected.value ?: return)
        }
    }

    fun selectDate(calendar: Calendar) = viewModelScope.launch {
        withContext(IO) {
            loading.postValue(true)
            selected.postValue(calendar)
            jots.postValue(
                QueriedJots(JotsByDateRange(app.db, calendar, calendar)).value()
            )
            loading.postValue(false)
        }
    }

    fun newJotsByImage(vararg uris: Uri) = viewModelScope.launch {
        loading.value = true
        val jots = HashMap<ExifInterface, ArrayList<Uri>>()
        uris.forEach { uri ->
            val uriExif = ExifInterface(app.contentResolver.openInputStream(uri) ?: return@forEach)
            val uriLatLong = uriExif.latLong ?: doubleArrayOf(MIN_VALUE, MIN_VALUE)
            val uriTime = exifTime(uriExif)
            jots.keys.forEach existForEach@{ existExif ->
                val existLatLong = existExif.latLong ?: doubleArrayOf(MIN_VALUE, MIN_VALUE)
                // @todo #1 Accurate distance determination
                // @todo #2 Configurable distance range and time range
                val distance = sqrt(
                    (existLatLong[0] - uriLatLong[0]).pow(2.0) +
                        (existLatLong[1] - uriLatLong[1]).pow(2.0)
                )
                val isLocationAllSet = existLatLong[0] != MIN_VALUE &&
                    existLatLong[1] != MIN_VALUE &&
                    uriLatLong[0] != MIN_VALUE &&
                    uriLatLong[1] != MIN_VALUE
                val isSameLocation = (!isLocationAllSet || distance < MeterDelta(30.0).value())
                if (isSameLocation &&
                    abs(uriTime - exifTime(existExif)) < 30 * 60 * 1000 // 30 min
                ) {
                    jots[existExif] = jots[existExif]?.apply { add(uri) } ?: arrayListOf(uri)
                    return@forEach
                }
            }
            jots[uriExif] = arrayListOf(uri)
        }
        jots.forEach { it ->
            val newJot = NewJot(
                app.db,
                "",
                "",
                it.key.latLong?.reversedArray() ?: doubleArrayOf(MIN_VALUE, MIN_VALUE),
                Calendar.getInstance().apply { timeInMillis = exifTime(it.key) },
                "",
                false
            ).value()
            NewAttachments(app.db, newJot.id(), it.value.map { it.toString() }.toTypedArray()).value()
        }
        if (jots.size > 0) {
            selectDate(Calendar.getInstance().apply {
                timeInMillis = exifTime(jots.keys.first())
            })
        }
        loading.value = false
    }

    private fun exifTime(exif: ExifInterface): Long {
        return ExifUnixTimeStamp(
            ExifAttribute(
                ConstSource(exif),
                ExifInterface.TAG_DATETIME_ORIGINAL
            )
        ).value().let {
            if (it > 0) {
                val tz = TimeZone.getDefault()
                it - tz.getOffset(Calendar.ZONE_OFFSET.toLong())
            } else {
                Calendar.getInstance().timeInMillis
            }
        }
    }

    fun preferSwitchListType() {
        if (listType.value == LIST) {
            listType.value = ListType.MAP
        }else{
            listType.value = LIST
        }
    }
}