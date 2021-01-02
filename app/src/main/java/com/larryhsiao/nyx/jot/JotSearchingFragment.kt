package com.larryhsiao.nyx.jot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.larryhsiao.nyx.NyxFragment
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.ViewModelFactory
import com.larryhsiao.nyx.core.jots.Jot

/**
 * Fragment for search jot by keyword.
 */
class JotSearchingFragment : NyxFragment() {
    private val viewModel by lazy {
        ViewModelProvider(
                this,
                ViewModelFactory(app)
        ).get(JotsViewModel::class.java).apply {
            preferJots("")
        }
    }

    private val adapter by lazy {
        JotsAdapter(
                app.db,
                lifecycleScope
        ) { PreferJotAction(this, it, ::toJotFragment).fire() }
    }

    private fun toJotFragment(jot: Jot) {
        findNavController().navigate(
                JotSearchingFragmentDirections.actionJotSearchingFragmentToJotFragment(
                    jot.id()
                )
        )
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
                R.layout.fragment_jots_search,
                container,
                false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.jotsSearching_jotsRecyclerView).adapter = adapter
        view.findViewById<EditText>(R.id.jotsSearching_searchKeyword).addTextChangedListener {
            viewModel.preferJots(it?.toString() ?: "")
        }
        viewModel.jots().observe(viewLifecycleOwner, adapter::load)
    }
}