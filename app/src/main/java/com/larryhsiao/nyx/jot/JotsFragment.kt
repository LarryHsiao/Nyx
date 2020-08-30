package com.larryhsiao.nyx.jot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.larryhsiao.nyx.NyxFragment
import com.larryhsiao.nyx.R
import kotlinx.android.synthetic.main.fragment_jots.*
import java.util.Calendar.*

/**
 * Fragment that shows all Jots.
 */
class JotsFragment : NyxFragment(), CalendarView.OnCalendarSelectListener {
    private val model by lazy { modelProvider.get(JotsViewModel::class.java) }
    private val adapter by lazy {
        JotsAdapter {
            toJotFragment(it.id())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_jots, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        jots_calendarView.setOnCalendarSelectListener(this)
        jots_recyclerView.adapter = adapter
        jots_newJot_floatingActionButton.setOnClickListener { toJotFragment(-1L) }
        jots_newJot_textView.setOnClickListener { toJotFragment(-1L) }
        model.loading().observe(viewLifecycleOwner, {
            if (it) {
                jots_newJot_textView.visibility = GONE
                jots_newJot_floatingActionButton.visibility = GONE
                jots_loadingBar.visibility = VISIBLE
            } else {
                jots_loadingBar.visibility = GONE
            }
        })
        model.jots().observe(viewLifecycleOwner, {
            adapter.load(it)
            if (it.isEmpty()) {
                jots_newJot_textView.visibility = VISIBLE
                jots_newJot_floatingActionButton.visibility = GONE
            } else {
                jots_newJot_textView.visibility = GONE
                jots_newJot_floatingActionButton.visibility = VISIBLE
            }
        })
        model.selected().observe(viewLifecycleOwner, {
            jots_calendarView.setSelectRangeMode()
            Calendar().apply {
                year = it.get(YEAR)
                month = it.get(MONTH) + 1
                day = it.get(DAY_OF_MONTH)
            }.let { jots_calendarView.setSelectCalendarRange(it, it) }
        })
        model.initJots()
    }

    private fun toJotFragment(id: Long) {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.jotFragment,
                Bundle().apply {
                    putLong("id", id)
                }
            )
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {
    }

    override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
        calendar ?: return
        if (isClick) {
            model.selectDate(java.util.Calendar.getInstance().apply {
                set(YEAR, calendar.year)
                set(MONTH, calendar.month - 1)
                set(DAY_OF_MONTH, calendar.day)
            })
        }
    }
}