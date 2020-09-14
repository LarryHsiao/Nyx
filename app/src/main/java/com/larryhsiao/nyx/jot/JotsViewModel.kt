package com.larryhsiao.nyx.jot

import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larryhsiao.nyx.JotApplication
import com.larryhsiao.nyx.core.attachments.NewAttachments
import com.larryhsiao.nyx.core.jots.Jot
import com.larryhsiao.nyx.core.jots.JotsByDate
import com.larryhsiao.nyx.core.jots.NewJot
import com.larryhsiao.nyx.core.jots.QueriedJots
import com.larryhsiao.nyx.core.jots.goemetry.MeterDelta
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.sqrt

/**
 * ViewModel to represent Jot list.
 */
class JotsViewModel(private val app: JotApplication) : ViewModel() {
    private val selected = MutableLiveData<Calendar>().apply {
        value = Calendar.getInstance()
    }
    private val jots = MutableLiveData<List<Jot>>()
    private val loading = MutableLiveData<Boolean>()

    fun loading(): LiveData<Boolean> = loading

    fun selected(): LiveData<Calendar> = selected

    fun jots(): LiveData<List<Jot>> = jots

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
                QueriedJots(
                    JotsByDate(
                        Date(calendar.timeInMillis),
                        app.db
                    )
                ).value()
            )
            loading.postValue(false)
        }
    }

    fun newJotsByImage(vararg uris: Uri) = viewModelScope.launch {
        loading.value = true
        val jots = HashMap<DoubleArray, ArrayList<Uri>>()
        uris.forEach { uri ->
            ExifInterface(
                app.contentResolver.openInputStream(uri) ?: return@forEach
            ).latLong?.let { y ->
                jots.keys.forEach existForEach@{ x ->
                    // @todo #1 Accurate distance determination
                    // @todo #2 Configurable distance range
                    val distance = sqrt((x[0] - y[0]) * (x[0] - y[0]) + (x[1] - y[1]) * (x[1] - y[1]))
                    if (distance < MeterDelta(30.0).value()) {
                        jots[x] = jots[x]?.apply { add(uri) } ?: arrayListOf(uri)
                        return@forEach
                    }
                }
                jots[y] = arrayListOf(uri)
            }
        }
        jots.forEach { it ->
            val newJot = NewJot(app.db, "", "", it.key.reversedArray()).value()
            NewAttachments(app.db, newJot.id(), it.value.map { it.toString() }.toTypedArray()).value()
            selectDate(selected.value ?: Calendar.getInstance())
        }
        loading.value = false
    }
}