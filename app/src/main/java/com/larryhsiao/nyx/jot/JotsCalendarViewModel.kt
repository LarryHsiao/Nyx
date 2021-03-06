package com.larryhsiao.nyx.jot

import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larryhsiao.aura.images.exif.ExifAttribute
import com.larryhsiao.aura.images.exif.ExifUnixTimeStamp
import com.larryhsiao.clotho.source.ConstSource
import com.larryhsiao.nyx.NyxApplication
import com.larryhsiao.nyx.core.attachments.ConstAttachment
import com.larryhsiao.nyx.core.jots.*
import com.larryhsiao.nyx.core.jots.goemetry.MeterDelta
import com.larryhsiao.nyx.jot.JotsCalendarViewModel.ListType.LIST
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.Double.Companion.MIN_VALUE
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * ViewModel to represent Jot list.
 */
class JotsCalendarViewModel(private val app: NyxApplication) : ViewModel() {
    companion object {
        private const val SAME_JOT_RANGE_MILLIS = 15 * 60 * 1000 // 15min
    }

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
            jots.postValue(app.nyx().jots().byDateRange(calendar, calendar))
            loading.postValue(false)
        }
    }

    fun newJotsByImage(vararg uris: Uri) = viewModelScope.launch {
        loading.value = true
        val exifToUri = HashMap<ExifInterface, ArrayList<Uri>>()
        uris.forEach { uri ->
            val uriExif = ExifInterface(app.contentResolver.openInputStream(uri) ?: return@forEach)
            val uriLatLong = uriExif.latLong ?: doubleArrayOf(MIN_VALUE, MIN_VALUE)
            val uriTime = exifTime(uriExif)
            exifToUri.keys.forEach existForEach@{ existExif ->
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
                    abs(uriTime - exifTime(existExif)) < SAME_JOT_RANGE_MILLIS // 30 min
                ) {
                    exifToUri[existExif] =
                        exifToUri[existExif]?.apply { add(uri) } ?: arrayListOf(uri)
                    return@forEach
                }
            }
            exifToUri[uriExif] = arrayListOf(uri)
        }
        exifToUri.forEach { exifUriEntry ->
            exifUriEntry.value.map { it.toString() }.forEach {
                app.nyx().attachments().newAttachment(
                    ConstAttachment(
                        -1,
                        app.nyx().jots().createByTimeSpace(
                            Calendar.getInstance().apply {
                                timeInMillis = exifTime(exifUriEntry.key)
                            },
                            (exifUriEntry.key.latLong?.reversedArray() ?: doubleArrayOf(
                                MIN_VALUE,
                                MIN_VALUE
                            )),
                            SAME_JOT_RANGE_MILLIS
                        ).id(),
                        it,
                        0,
                        0
                    )
                )
            }
        }
        if (exifToUri.size > 0) {
            selectDate(Calendar.getInstance().apply {
                timeInMillis = exifTime(exifToUri.keys.first())
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
        } else {
            listType.value = LIST
        }
    }
}