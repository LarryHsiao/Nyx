package com.larryhsiao.nyx.jot

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
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
class JotFragment : NyxFragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var mainScope: CoroutineScope
    private val dateFormatter by lazy {
        SimpleDateFormat("d MMM yyyy | hh:mm a", Locale.getDefault())
    }
    private val jotViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(app)).get(JotViewModel::class.java)
    }
    private val jotsViewModel by lazy {
        ViewModelProvider(requireActivity(), ViewModelFactory(app)).get(JotsViewModel::class.java)
    }
    private val datePicker by lazy {
        val current = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            this,
            current.get(Calendar.YEAR),
            current.get(Calendar.MONTH),
            current.get(Calendar.DAY_OF_MONTH),
        ).apply {
            setButton(
                BUTTON_NEGATIVE,
                getString(R.string.Today)
            ) { _, _ ->
                onDateSet(
                    this.datePicker,
                    current.get(Calendar.YEAR),
                    current.get(Calendar.MONTH),
                    current.get(Calendar.DAY_OF_MONTH),
                )
            }
        }
    }
    private val timePicker by lazy {
        val current = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            this,
            current.get(Calendar.HOUR_OF_DAY),
            current.get(Calendar.MINUTE),
            false
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_jot, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainScope = MainScope()
        jot_title_right_textView.setOnClickListener { save() }
        jot_calendar_imageView.setOnClickListener { showDatePicker() }
        jot_clock_imageView.setOnClickListener { showTimePicker() }
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

    private fun showDatePicker(){
        val currentDate = Calendar.getInstance().apply {
            time = dateFormatter.parse(jot_datetime_textView.text.toString()) ?: Date()
        }
        datePicker.updateDate(
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH),
        )
        datePicker.show()
    }

    private fun showTimePicker(){
        val currentDate = Calendar.getInstance().apply {
            time = dateFormatter.parse(jot_datetime_textView.text.toString()) ?: Date()
        }
        timePicker.updateTime(
            currentDate.get(Calendar.HOUR_OF_DAY),
            currentDate.get(Calendar.MINUTE)
        )
        timePicker.show()
    }


    private fun save() = mainScope.launch {
        withContext(Default) {
            jotViewModel.save(object : WrappedJot(jotViewModel.jot().value) {
                override fun content(): String {
                    return jot_content_editText.text.toString()
                }

                override fun title(): String {
                    return jot_title_editText.text.toString()
                }

                override fun createdTime(): Long {
                    return dateFormatter.parse(jot_datetime_textView.text.toString())?.time ?: 0L
                }
            })
            jotsViewModel.reload()
        }
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainScope.cancel()
    }

    private fun formattedDate(time: Long): String {
        return dateFormatter.format(
            Date(time)
        )
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        jot_datetime_textView.text = formattedDate(Calendar.getInstance().apply {
            time = dateFormatter.parse(jot_datetime_textView.text.toString()) ?: Date()
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }.timeInMillis)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        jot_datetime_textView.text = formattedDate(Calendar.getInstance().apply {
            time = dateFormatter.parse(jot_datetime_textView.text.toString()) ?: Date()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }.timeInMillis)
    }
}