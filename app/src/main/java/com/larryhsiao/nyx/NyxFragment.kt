package com.larryhsiao.nyx

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

/**
 * Base Fragment for Nyx
 */
abstract class NyxFragment : Fragment() {
    val app by lazy {
        requireContext().applicationContext as JotApplication
    }
    val modelProvider by lazy {
        ViewModelProviders.of(
            this,
            ViewModelFactory(app)
        )
    }
}