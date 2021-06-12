package com.larryhsiao.nyx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.larryhsiao.nyx.jot.JotViewModel
import com.larryhsiao.nyx.jot.JotsCalendarViewModel
import com.larryhsiao.nyx.jot.JotsSearchingViewModel
import com.larryhsiao.nyx.jot.JotsViewModel
import com.larryhsiao.nyx.old.sync.LocalFileSync

/**
 * Factory to build ViewModel for Nyx.
 */
class ViewModelFactory(private val app: NyxApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(JotsCalendarViewModel::class.java) -> JotsCalendarViewModel(app) as T
            modelClass.isAssignableFrom(JotViewModel::class.java) -> JotViewModel(app.nyx(), LocalFileSync(app, app.nyx())) as T
            modelClass.isAssignableFrom(JotsViewModel::class.java) -> JotsViewModel(app) as T
            modelClass.isAssignableFrom(JotsSearchingViewModel::class.java) -> JotsSearchingViewModel(app) as T
            else -> modelClass.getConstructor().newInstance()
        }
    }
}