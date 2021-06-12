package com.larryhsiao.nyx.jot

import android.Manifest.permission.CAMERA
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.DatePickerDialog.BUTTON_NEGATIVE
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.larryhsiao.nyx.BuildConfig
import com.larryhsiao.nyx.NyxFragment
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.ViewModelFactory
import com.larryhsiao.nyx.core.jots.Jot
import com.larryhsiao.nyx.old.attachments.AttachmentPickerIntent
import com.larryhsiao.nyx.old.attachments.TempAttachmentFile
import kotlinx.android.synthetic.main.fragment_jots_calendar.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Double.Companion.MIN_VALUE

/**
 * Fragment that shows all Jots.
 */
class JotsCalendarFragment : NyxFragment(), CalendarView.OnCalendarSelectListener {
    companion object {
        private const val REQUEST_CODE_NEW_JOT_BY_IMAGES = 1000
        private const val REQUEST_CODE_CAPTURE_PHOTO = 1001
        private const val REQUEST_CODE_PERMISSION_CAMERA_FOR_NEW_JOT = 1002

        /**
         *  Use fixed file path as camera output.
         */
        private const val TEMP_FILE_PHOTO_CAPTURE = "TEMP_FILE_PHOTO_CAPTURE"
    }

    private val photoTempFile by lazy { TempAttachmentFile(requireContext(), TEMP_FILE_PHOTO_CAPTURE).value() }
    private val dateFormat by lazy { SimpleDateFormat("yyyy MM", Locale.getDefault()) }
    private val dayFormat by lazy { SimpleDateFormat("MM/dd", Locale.getDefault()) }
    private val model by lazy {
        ViewModelProvider(
                requireActivity(),
                ViewModelFactory(app)
        ).get(JotsCalendarViewModel::class.java)
    }
    private val adapter by lazy {
        JotsAdapter(
                app.nyx(),
                lifecycleScope
        ) { PreferJotAction(this, it, ::toJotFragment).fire() }
    }
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
    ): View? = inflater.inflate(R.layout.fragment_jots_calendar, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        jots_calendarView.setOnCalendarSelectListener(this)
        jots_calendarView.setOnMonthChangeListener(::onMonthChanged)
        jots_recyclerView.adapter = adapter
        jots_newJot_imageView.setOnClickListener { toNewJotFragment() }
        jots_newJotByImage_imageView.setOnClickListener(::newJotByImages)
        jots_newJotByCamera_imageView.setOnClickListener(::newJotByCamera)
        jots_month_textView.setOnClickListener(::onMonthIndicatorClicked)
        jot_list_map_switcher_imageView.setOnClickListener(::onSwitchListMap)
        jots_searchButton.setOnClickListener(::searchJots)
        model.loading().observe(viewLifecycleOwner) {
            if (it) {
                jots_blackhole_textView.visibility = GONE
                jots_loadingBar.visibility = VISIBLE
            } else {
                jots_loadingBar.visibility = GONE
            }
        }
        model.jots().observe(viewLifecycleOwner, ::loadJots)
        model.selected().observe(viewLifecycleOwner) {
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
        }
        model.listType().observe(viewLifecycleOwner) {
            when (it) {
                JotsCalendarViewModel.ListType.LIST -> loadList()
                JotsCalendarViewModel.ListType.MAP -> loadMap()
            }
        }
        model.initJots()
    }

    private fun searchJots(view: View) {
        findNavController().navigate(
                JotsCalendarFragmentDirections.actionJotsCalendarFragmentToJotSearchingFragment()
        )
    }

    override fun onPause() {
        super.onPause()
        childFragmentManager.findFragmentById(R.id.jots_map)?.let { mapFrag ->
            childFragmentManager.beginTransaction().remove(mapFrag).commit()
        }
    }

    private fun loadJots(it: List<Jot>) {
        if (model.listType().value == JotsCalendarViewModel.ListType.LIST) {
            loadList(it)
        } else {
            loadMap(it)
        }
    }

    private fun loadList() {
        loadList(model.jots().value ?: emptyList())
    }

    private fun loadList(it: List<Jot>) {
        adapter.load(it)
        jot_list_map_switcher_imageView.setImageResource(R.drawable.ic_map)
        if (it.isEmpty()) {
            jots_blackhole_textView.visibility = VISIBLE
        } else {
            jots_blackhole_textView.visibility = GONE
        }
        childFragmentManager.findFragmentById(R.id.jots_map)?.let {
            childFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    private fun loadMap() {
        loadMap(model.jots().value ?: emptyList())
    }

    private fun loadMap(it: List<Jot>) {
        jots_blackhole_textView.visibility = GONE
        jot_list_map_switcher_imageView.setImageResource(R.drawable.ic_agenda)
        val manager = childFragmentManager
        val mapFrag = manager.findFragmentById(R.id.jots_map) as? SupportMapFragment
                ?: SupportMapFragment.newInstance().apply {
                    manager.beginTransaction().replace(R.id.jots_map, this).commit()
                }
        mapFrag.getMapAsync(::loadUpMap)
    }

    private fun loadUpMap(map: GoogleMap) {
        val clusterManager = ClusterManager<JotMapItem>(requireContext(), map)
        clusterManager.renderer = DefaultClusterRenderer(
                requireContext(),
                map,
                clusterManager
        ).apply { minClusterSize = 2 }
        map.setOnCameraIdleListener {
            clusterManager.onCameraIdle()
            map.resetMinMaxZoomPreference()
        }
        map.setOnMarkerClickListener(clusterManager)
        map.setOnInfoWindowClickListener(clusterManager)
        clusterManager.setOnClusterItemInfoWindowClickListener { toJotFragment(it.jot) }
        clusterManager.setOnClusterClickListener {
            toJotsFragment(it.items.map { it.jot.id() })
            true
        }
        loadUpMapMarkers(map, clusterManager)
    }

    private fun toJotsFragment(jotIds: List<Long>) {
        findNavController().navigate(
                JotsCalendarFragmentDirections.actionJotsCalendarFragmentToJotsFragment(
                        jotIds.toLongArray()
                )
        )
    }

    private fun loadUpMapMarkers(map: GoogleMap, clusterManager: ClusterManager<JotMapItem>) {
        val latLngBounds = LatLngBounds.Builder()
        var haveLocation = false
        map.clear()
        clusterManager.clearItems()
        model.jots().value?.filter {
            !it.location().contentEquals(doubleArrayOf(MIN_VALUE, MIN_VALUE)) &&
                    !it.location().contentEquals(doubleArrayOf(0.0, 0.0))
        }?.forEach { jot ->
            val position = LatLng(jot.location()[1], jot.location()[0])
            clusterManager.addItem(JotMapItem(jot))
            latLngBounds.include(position)
            haveLocation = true
        }
        if (haveLocation) {
            map.setMaxZoomPreference(15f)
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 200))
        } else {
            map.animateCamera(CameraUpdateFactory.zoomTo(10f))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_NEW_JOT_BY_IMAGES && resultCode == RESULT_OK) {
            data ?: return
            val pickedUris = ArrayList<Uri>()
            val pickedUri = data.data
            if (pickedUri != null) {
                requestFilePermission(pickedUri)
                pickedUris.add(pickedUri)
            } else {
                val clip = data.clipData ?: return
                for (i in 0 until clip.itemCount) {
                    requestFilePermission((clip.getItemAt(i).uri))
                    pickedUris.add(clip.getItemAt(i).uri)
                }
            }
            model.newJotsByImage(*pickedUris.toTypedArray())
        } else if (requestCode == REQUEST_CODE_CAPTURE_PHOTO && resultCode == RESULT_OK) {
            if (photoTempFile.exists()) {
                val tempFile = TempAttachmentFile(
                        requireContext(),
                        "" + System.currentTimeMillis() + ".jpg"
                ).value()
                photoTempFile.renameTo(tempFile)
                model.newJotsByImage(
                        FileProvider.getUriForFile(
                                requireContext(),
                                BuildConfig.FILE_PROVIDER_AUTHORITY,
                                tempFile
                        )
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION_CAMERA_FOR_NEW_JOT &&
                grantResults[0] == PERMISSION_GRANTED
        ) {
            newJotByCamera(requireContext())
        }
    }

    private fun requestFilePermission(uri: Uri) {
        try {
            requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun newJotByCamera(view: View) {
        newJotByCamera(view.context)
    }

    private fun newJotByCamera(context: Context) {
        try {
            if (ContextCompat.checkSelfPermission(context, CAMERA) != PERMISSION_GRANTED) {
                requestPermissions(
                        arrayOf(CAMERA),
                        REQUEST_CODE_PERMISSION_CAMERA_FOR_NEW_JOT
                )
                return
            }
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (
                    intent.resolveActivity(requireContext().packageManager) !=
                    null
            ) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
                        requireContext(),
                        BuildConfig.FILE_PROVIDER_AUTHORITY,
                        photoTempFile
                ))
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_PHOTO)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @Suppress("UNUSED_PARAMETER")
    private fun newJotByImages(it: View) {
        startActivityForResult(
                AttachmentPickerIntent().value(),
                REQUEST_CODE_NEW_JOT_BY_IMAGES
        )
    }

    private fun toNewJotFragment() {
        findNavController().navigate(
                JotsCalendarFragmentDirections.actionJotsCalendarFragmentToNewJotFragment(
                        model.selected().value ?: java.util.Calendar.getInstance()
                )
        )
    }

    private fun toJotFragment(jot: Jot) {
        findNavController().navigate(
                JotsCalendarFragmentDirections.actionJotsCalendarFragmentToJotFragment(jot.id())
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
        val selected = model.selected().value ?: java.util.Calendar.getInstance()
        datePicker.updateDate(
                selected.get(java.util.Calendar.YEAR),
                selected.get(java.util.Calendar.MONTH),
                selected.get(java.util.Calendar.DAY_OF_MONTH),
        )
        datePicker.show()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onDatePickerSelected(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        model.selectDate(java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, year)
            set(java.util.Calendar.MONTH, month)
            set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth)
        })
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onTodaySelected(dialog: DialogInterface, which: Int) {
        model.selectDate(java.util.Calendar.getInstance())
    }

    private fun onSwitchListMap(view: View) {
        model.preferSwitchListType()
    }
}