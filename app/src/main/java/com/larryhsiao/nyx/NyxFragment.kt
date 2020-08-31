package com.larryhsiao.nyx

import androidx.fragment.app.Fragment

/**
 * Base Fragment for Nyx
 */
abstract class NyxFragment : Fragment() {
    val app by lazy {
        requireContext().applicationContext as JotApplication
    }
}