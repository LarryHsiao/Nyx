package com.larryhsiao.nyx.jot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larryhsiao.nyx.NyxApplication
import com.larryhsiao.nyx.core.jots.Jot
import com.larryhsiao.nyx.core.jots.JotsByIds
import com.larryhsiao.nyx.core.jots.JotsByKeyword
import com.larryhsiao.nyx.core.jots.QueriedJots
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModels for jot list.
 */
class JotsViewModel(private val app: NyxApplication) : ViewModel() {
    private val jots = MutableLiveData<List<Jot>>()

    fun jots(): LiveData<List<Jot>> = jots

    fun preferAllJots() = preferJots("")

    fun preferJots(ids: LongArray) = viewModelScope.launch {
        jots.value = withContext(IO) { QueriedJots(JotsByIds(app.db, ids)).value() }
    }

    fun preferJots(keyword: String) = viewModelScope.launch {
        jots.value = withContext(IO) {
            QueriedJots(JotsByKeyword(app.db, keyword)).value()
        }
    }
}