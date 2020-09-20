package com.larryhsiao.nyx.jot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.larryhsiao.nyx.NyxFragment
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_jots.*

class JotsFragment : NyxFragment() {
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelFactory(app)
        ).get(JotsViewModel::class.java).apply {
            preferJots(JotsFragmentArgs.fromBundle(requireArguments()).ids)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_jots, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = JotsAdapter {
            findNavController().navigate(
                JotsFragmentDirections.actionJotsFragmentToJotFragment(
                    it.id()
                )
            )
        }
        jots_recyclerView.adapter = adapter
        viewModel.jots().observe(viewLifecycleOwner, adapter::load)
    }
}