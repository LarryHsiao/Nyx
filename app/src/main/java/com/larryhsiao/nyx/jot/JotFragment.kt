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
import com.larryhsiao.nyx.core.jots.WrappedJot
import kotlinx.android.synthetic.main.fragment_jot.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment for representing a Jot.
 */
class JotFragment : NyxFragment() {
    private lateinit var mainScope: CoroutineScope
    private val jotViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(app)).get(JotViewModel::class.java)
    }
    private val jotsViewModel by lazy {
        ViewModelProvider(requireActivity(), ViewModelFactory(app)).get(JotsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_jot, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainScope = MainScope()
        jot_title_right_textView.setOnClickListener {
            mainScope.launch {
                withContext(Default) {
                    jotViewModel.save(object : WrappedJot(jotViewModel.jot().value) {
                        override fun content(): String {
                            return jot_content_editText.text.toString()
                        }

                        override fun title(): String {
                            return jot_title_editText.text.toString()
                        }
                    })
                    jotsViewModel.reload()
                }
                findNavController().popBackStack()
            }
        }
        jotViewModel.jot().observe(viewLifecycleOwner, {
            jot_datetime_textView.text = formattedDate(it.createdTime())
            jot_title_editText.setText(it.title())
            jot_content_editText.setText(it.content())
        })
        jotViewModel.isNewJot().observe(viewLifecycleOwner, {
            jot_title_bar_title_textView.text = if (it) {
                getString(R.string.New_Jot)
            } else {
                getString(R.string.Edit)
            }
        })
        jotViewModel.loadJot(requireArguments().getLong("id"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainScope.cancel()
    }

    private fun formattedDate(time: Long): String {
        return SimpleDateFormat("d MMM yyyy | hh:mm a", Locale.getDefault()).format(
            Date(time)
        )
    }
}