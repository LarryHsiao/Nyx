package com.larryhsiao.nyx.jot

import android.app.DatePickerDialog
import android.app.DatePickerDialog.BUTTON_NEGATIVE
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.larryhsiao.nyx.NyxFragment
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.ViewModelFactory
import com.larryhsiao.nyx.old.sync.SyncService
import kotlinx.android.synthetic.main.fragment_jots.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment that shows all Jots.
 */
class JotsFragment : NyxFragment(), CalendarView.OnCalendarSelectListener {
    private val dateFormat by lazy { SimpleDateFormat("yyyy MM", Locale.getDefault()) }
    private val dayFormat by lazy { SimpleDateFormat("MM/dd", Locale.getDefault()) }
    private val model by lazy {
        ViewModelProvider(requireActivity(), ViewModelFactory(app)).get(JotsViewModel::class.java)
    }
    private val adapter by lazy { JotsAdapter { toJotFragment(it.id()) } }
    private val datePicker by lazy {
        DatePickerDialog(
            requireContext(),
            0,
            ::onDatePickerSelected,
            java.util.Calendar.getInstance().get(java.util.Calendar.YEAR),
            java.util.Calendar.getInstance().get(java.util.Calendar.MONTH),
            java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH),
        ).apply {
            setButton(BUTTON_NEGATIVE, getString(R.string.Today), ::onTodaySelected)
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
        jots_calendarView.setOnMonthChangeListener(::onMonthChanged)
        jots_recyclerView.adapter = adapter
        jots_newJot_floatingActionButton.setOnClickListener { toNewJotFragment() }
        jots_newJot_textView.setOnClickListener { toNewJotFragment() }
        jots_month_textView.setOnClickListener(::onMonthIndicatorClicked)
        jots_newJot_textView.setOnLongClickListener {
            findNavController().navigate(R.id.cloudFragment)
            SyncService.enqueue(it.context)
            true
        }
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
                year = it.get(java.util.Calendar.YEAR)
                month = it.get(java.util.Calendar.MONTH) + 1
                day = it.get(java.util.Calendar.DAY_OF_MONTH)
            }.let {
                jots_month_textView.text = dateFormat.format(Date(it.timeInMillis))
                jots_calendarView.setSelectCalendarRange(it, it)
                jots_day_textView.text = dayFormat.format(Date(it.timeInMillis))
            }
        })
        model.initJots()
    }

    private fun toNewJotFragment() {
        Navigation.findNavController(requireView()).navigate(
            R.id.newJotFragment,
            Bundle().apply {
                putSerializable(
                    "date",
                    model.selected().value ?: java.util.Calendar.getInstance()
                )
            }
        )
    }

    private fun toJotFragment(id: Long) {
        Navigation.findNavController(requireView()).navigate(
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
            java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.YEAR, calendar.year)
                set(java.util.Calendar.MONTH, calendar.month - 1)
                set(java.util.Calendar.DAY_OF_MONTH, calendar.day)
            }.let { model.selectDate(it) }
        }
    }

    private fun onMonthChanged(year: Int, month: Int) {
        java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, year)
            set(java.util.Calendar.MONTH, month - 1)
        }.let {
            jots_month_textView.text = dateFormat.format(it.time)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onMonthIndicatorClicked(view: View?) {
        val selected =model.selected().value?:java.util.Calendar.getInstance()
        datePicker.updateDate(
            selected.get(java.util.Calendar.YEAR),
            selected.get(java.util.Calendar.MONTH),
            selected.get(java.util.Calendar.DAY_OF_MONTH),
        )
        datePicker.show()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onDatePickerSelected(view : DatePicker,  year:Int, month:Int,  dayOfMonth:Int){
        model.selectDate(java.util.Calendar.getInstance().apply{
            set(java.util.Calendar.YEAR, year)
            set(java.util.Calendar.MONTH, month)
            set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth)
        })
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onTodaySelected(dialog: DialogInterface, which:Int){
        model.selectDate(java.util.Calendar.getInstance())
    }
}