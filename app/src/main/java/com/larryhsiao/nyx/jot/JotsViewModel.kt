package com.larryhsiao.nyx.jot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larryhsiao.nyx.JotApplication
import com.larryhsiao.nyx.core.jots.Jot
import com.larryhsiao.nyx.core.jots.JotsByIds
import com.larryhsiao.nyx.core.jots.QueriedJots
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JotsViewModel(private val app: JotApplication) : ViewModel() {
    private val jots = MutableLiveData<List<Jot>>()

    fun jots(): LiveData<List<Jot>> = jots

    fun preferJots(ids: LongArray) = viewModelScope.launch{
        jots.value = withContext(IO){ QueriedJots(JotsByIds(app.db, ids)).value() }
    }
}