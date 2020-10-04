package com.larryhsiao.nyx.jot

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.larryhsiao.clotho.openweather.Weather
import com.larryhsiao.clotho.openweather.Weather.Type
import com.larryhsiao.clotho.openweather.Weather.Type.*
import com.larryhsiao.nyx.NyxFragment
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.ViewModelFactory
import com.larryhsiao.nyx.old.attachments.AttachmentsFragment
import com.larryhsiao.nyx.old.util.JpegDateComparator
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LONGITUDE
import com.schibstedspain.leku.LocationPickerActivity
import com.silverhetch.aura.view.alert.Alert
import com.silverhetch.clotho.temperature.FahrenheitCelsius
import kotlinx.android.synthetic.main.dialog_weather.*
import kotlinx.android.synthetic.main.fragment_jot.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors
import kotlin.Double.Companion.MIN_VALUE

/**
 * Fragment for representing a Jot.
 */
class JotFragment : NyxFragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    companion object {
        private const val REQUEST_CODE_LOCATION_PICKER: Int = 1000
        private const val REQUEST_CODE_ATTACHMENT_DIALOG: Int = 1001
    }

    private val dateFormatter by lazy {
        SimpleDateFormat("d MMM yyyy | hh:mm a", Locale.getDefault())
    }
    private val jotViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(app)).get(JotViewModel::class.java)
    }
    private val jotsViewModel by lazy {
        ViewModelProvider(requireActivity(), ViewModelFactory(app)).get(JotsCalendarViewModel::class.java)
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

    private val deleteConfirmationDialog by lazy {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete)
            .setMessage(R.string.delete_this_diary)
            .setNegativeButton(R.string.No) { _, _ -> }
            .setPositiveButton(R.string.delete) { _, _ ->
                lifecycleScope.launch {
                    // TODO: Screen locking
                    jotViewModel.delete()
                    reloadJotsByJotTime()
                    findNavController().popBackStack()
                }
            }
            .create()
    }

    private val discardConfirmationDialog by lazy {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.discard)
            .setMessage(R.string.Are_you_sure_to_discard_)
            .setNegativeButton(R.string.No) { _, _ -> }
            .setPositiveButton(R.string.discard) { _, _ ->
                findNavController().popBackStack()
            }
            .create()
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

    private val onBackCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (jotViewModel.isModified().value == true) {
                    discardConfirmationDialog.show()
                } else {
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_jot, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        jot_title_right_textView.setOnClickListener { save() }
        jot_title_left_textView.setOnClickListener { delete() }
        jot_calendar_imageView.setOnClickListener { showDatePicker() }
        jot_clock_imageView.setOnClickListener { showTimePicker() }
        jot_weather_imageView.setOnClickListener(::showWeatherInfo)
        jot_location_imageView.setOnClickListener { showLocationPicker() }
        jot_location_imageView.setOnLongClickListener {
            val currentLocation = jotViewModel.location().value
            if (currentLocation != null && !currentLocation.contentEquals(
                    doubleArrayOf(MIN_VALUE, MIN_VALUE)
                )) {
                promoteRemoveLocation()
            } else {
                showLocationPicker()
            }
            true
        }
        jot_image_imageView.setOnClickListener { showImages() }
        jot_title_editText.doAfterTextChanged { jotViewModel.preferTitle(it?.toString() ?: "") }
        jot_content_editText.doAfterTextChanged { jotViewModel.preferContent(it?.toString() ?: "") }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackCallback);
        jotViewModel.isNewJot().observe(viewLifecycleOwner, {
            jot_title_bar_title_textView.text = if (it) {
                getString(R.string.New_Jot)
            } else {
                getString(R.string.Edit)
            }
        })
        jotViewModel.title().observe(viewLifecycleOwner, {
            if (it != jot_title_editText.text.toString()) {
                jot_title_editText.setText(it)
            }
        })
        jotViewModel.content().observe(viewLifecycleOwner, {
            if (it != jot_content_editText.text.toString()) {
                jot_content_editText.setText(it)
            }
        })
        jotViewModel.isNewJot().observe(viewLifecycleOwner, {
            jot_title_left_textView.text = if (it == true) {
                getString(R.string.discard)
            } else {
                getString(R.string.delete)
            }
        })
        jotViewModel.time().observe(viewLifecycleOwner, { jot_datetime_textView.text = formattedDate(it) })
        jotViewModel.location().observe(viewLifecycleOwner, ::loadUpLocation)
        jotViewModel.attachments().observe(viewLifecycleOwner, ::loadUpAttachments)
        jotViewModel.weather().observe(viewLifecycleOwner, ::loadUpWeather)
        if (requireArguments().containsKey("id")) {
            jotViewModel.loadJot(requireArguments().getLong("id"))
        } else {
            jotViewModel.newJot(
                (requireArguments().getSerializable("date") as? Calendar) ?: Calendar.getInstance()
            )
        }
    }

    private fun showWeatherInfo(view: View) {
        AlertDialog.Builder(view.context)
            .setView(R.layout.dialog_weather)
            .show().apply {
                val weather = jotViewModel.weather().value ?: return
                this.dialogWeather_weather_imageView.setImageResource(
                    weatherIconByType(weather.type())
                )
                this.dialogWeather_info.text = getString(
                    R.string.Temperature___,
                    FahrenheitCelsius(weather.temperature().toFloat()).value().toInt().toString()
                )
                this.dialogWeather_info.append("\n")
                this.dialogWeather_info.append(getString(R.string.Humidity_____) + " ")
                this.dialogWeather_info.append(weather.humidity().toInt().toString() + "%")
            }
    }

    private fun promoteRemoveLocation() {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.Remove_location_))
            .setPositiveButton(R.string.confirm) { _, _ ->
                jotViewModel.preferLocation(doubleArrayOf(MIN_VALUE, MIN_VALUE))
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun loadUpWeather(weather: Weather?) {
        if (weather == null) {
            jot_weather_imageView.visibility = View.GONE
        } else {
            jot_weather_imageView.visibility = View.VISIBLE
            jot_weather_imageView.setImageResource(weatherIconByType(weather.type()))
        }
    }

    private fun weatherIconByType(type: Type): Int {
        return when (type) {
            CLEAR -> R.drawable.ic_clear_day
            CLOUDS -> R.drawable.ic_clouds
            DRIZZLE -> R.drawable.ic_drizzle
            RAIN -> R.drawable.ic_rain
            THUNDERSTORM -> R.drawable.ic_thunderstorm
            ATMOSPHERE -> R.drawable.ic_atmosphere
            SNOW -> R.drawable.ic_snow
        }
    }

    private fun delete() {
        if (jotViewModel.isNewJot().value == true) {
            if (jotViewModel.isModified().value == true) {
                discardConfirmationDialog.show()
            } else {
                findNavController().popBackStack()
            }
        } else {
            deleteConfirmationDialog.show()
        }
    }

    private fun loadUpAttachments(attachments: List<String>) {
        ImageViewCompat.setImageTintList(
            jot_image_imageView,
            ColorStateList.valueOf(
                if (attachments.isNotEmpty()) {
                    resources.getColor(R.color.colorPrimary)
                } else {
                    Color.parseColor("#000000")
                }
            )
        )
    }

    private fun showImages() {
        val sorted: List<Uri> = (
            jotViewModel.attachments().value?.map {
                Uri.parse(it)
            } ?: emptyList()
            ).stream()
            .sorted(JpegDateComparator(requireContext()))
            .collect(Collectors.toList())
        val dialog = AttachmentsFragment.newInstance(sorted, "")
        dialog.setTargetFragment(this, REQUEST_CODE_ATTACHMENT_DIALOG)
        dialog.show(parentFragmentManager, null)
    }

    private fun showDatePicker() {
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

    private fun showTimePicker() {
        val currentDate = Calendar.getInstance().apply {
            time = dateFormatter.parse(jot_datetime_textView.text.toString()) ?: Date()
        }
        timePicker.updateTime(
            currentDate.get(Calendar.HOUR_OF_DAY),
            currentDate.get(Calendar.MINUTE)
        )
        timePicker.show()
    }


    private fun save() = lifecycleScope.launch {
        jot_title_right_textView.isEnabled = false
        jotViewModel.save()
        reloadJotsByJotTime()
        jot_title_right_textView.isEnabled = true
        findNavController().popBackStack()
    }

    private fun reloadJotsByJotTime() {
        jotsViewModel.selectDate(Calendar.getInstance().apply {
            time = Date(jotViewModel.time().value ?: Date().time)
        })
    }

    private fun loadUpLocation(location: DoubleArray) {
        ImageViewCompat.setImageTintList(
            jot_location_imageView,
            ColorStateList.valueOf(
                if (isLocationSet(location)) {
                    resources.getColor(R.color.colorPrimary)
                } else {
                    Color.parseColor("#000000")
                }
            )
        )
    }

    private fun isLocationSet(location: DoubleArray): Boolean {
        return !location.contentEquals(doubleArrayOf(MIN_VALUE, MIN_VALUE)) &&
            !location.contentEquals(doubleArrayOf(0.0, 0.0))
    }

    private fun showLocationPicker() {
        val location = jotViewModel.location().value ?: doubleArrayOf(MIN_VALUE, MIN_VALUE)
        val build = LocationPickerActivity.Builder()
            .withGooglePlacesEnabled()
        if (isLocationSet(location)) {
            build.withLocation(location[1], location[0])
        }
        startActivityForResult(
            build.build(requireContext()),
            REQUEST_CODE_LOCATION_PICKER
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LOCATION_PICKER && resultCode == RESULT_OK) {
            if (data == null) {
                return
            }
            jotViewModel.preferLocation(doubleArrayOf(
                data.getDoubleExtra(LONGITUDE, 0.0),
                data.getDoubleExtra(LATITUDE, 0.0)
            ))
        } else if (requestCode == REQUEST_CODE_ATTACHMENT_DIALOG && resultCode == RESULT_OK) {
            if (data == null) {
                return
            }
            jotViewModel.preferAttachments(
                data.getParcelableArrayListExtra<Uri>("ARG_ATTACHMENT_URI") ?: emptyList()
            )
        }
    }

    private fun formattedDate(time: Long): String {
        return dateFormatter.format(Date(time))
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        jotViewModel.preferTime(Calendar.getInstance().apply {
            time = dateFormatter.parse(jot_datetime_textView.text.toString()) ?: Date()
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }.timeInMillis)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        jotViewModel.preferTime(Calendar.getInstance().apply {
            time = dateFormatter.parse(jot_datetime_textView.text.toString()) ?: Date()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }.timeInMillis)
    }
}