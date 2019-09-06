package com.larryhsiao.nyx.view.tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.silverhetch.aura.AuraFragment
import com.larryhsiao.nyx.R

/**
 * Fragment of Tag list.
 */
class TagListFragment : AuraFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tag_list, container, false)
    }
}