package com.larryhsiao.nyx.jot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.larryhsiao.nyx.NyxFragment
import com.larryhsiao.nyx.R
import kotlinx.android.synthetic.main.fragment_jot.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment for representing a Jot.
 */
class JotFragment : NyxFragment() {
    private val viewModel by lazy { modelProvider.get(JotViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_jot, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.jot().observe(viewLifecycleOwner, {
            jot_datetime_textView.text = formattedDate(it.createdTime())
            jot_content_editText.setText(it.content())
        })
        viewModel.loadJot(requireArguments().getLong("id"))
    }

    private fun formattedDate(time: Long): String {
        return SimpleDateFormat("d MMM yyyy | hh:mm a", Locale.getDefault()).format(
            Date(time)
        )
    }
}