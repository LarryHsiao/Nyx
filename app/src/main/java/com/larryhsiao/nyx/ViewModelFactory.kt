package com.larryhsiao.nyx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.larryhsiao.nyx.jot.JotsViewModel

/**
 * Factory to build ViewModel for Nyx.
 */
class ViewModelFactory(private val app:JotApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(JotsViewModel::class.java)) {
            JotsViewModel(app.db) as T
        } else {
            modelClass.getConstructor().newInstance()
        }
    }
}