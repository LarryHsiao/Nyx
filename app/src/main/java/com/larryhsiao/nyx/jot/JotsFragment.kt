package com.larryhsiao.nyx.jot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.larryhsiao.nyx.NyxFragment
import com.larryhsiao.nyx.R

/**
 * Fragment that shows all Jots.
 */
class JotsFragment : NyxFragment() {
    private val model by lazy { viewModelProvider.get(JotsViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_jots, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.jots().observe(viewLifecycleOwner, {
            it.forEach {
                System.out.println(it.content())
            }
        })
        model.fetch()
    }
}