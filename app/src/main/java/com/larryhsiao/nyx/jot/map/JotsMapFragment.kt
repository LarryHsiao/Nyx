package com.larryhsiao.nyx.jot.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.larryhsiao.clotho.date.DateCalendar
import com.larryhsiao.nyx.NyxFragment
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.ViewModelFactory
import com.larryhsiao.nyx.core.jots.Jot
import com.larryhsiao.nyx.utils.DateRangeText

class JotsMapFragment : NyxFragment() {
    private val viewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ViewModelFactory(app)
        ).get(JotsMapViewModel::class.java)
    }
    private var lastCameraPosition: CameraPosition? = null
    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.page_jots_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dateRangeLiveData().observe(viewLifecycleOwner) { setupDateRangeText(it, view) }
        setupDateRangeText(kotlin.Pair(0L, 0L), view)
        childFragmentManager.beginTransaction()
            .replace(
                R.id.jotsMap_mapContainer,
                SupportMapFragment.newInstance().apply {
                    getMapAsync {
                        map = it
                        loadUpMap(it)
                    }
                }
            )
            .commit()
    }

    private fun setupDateRangeText(it: kotlin.Pair<Long, Long>, view: View) {
        val dateRangeText = view.findViewById<TextView>(R.id.dateRangeIndicator_dateRangeText)
        val dateRangeIcon = view.findViewById<ImageView>(R.id.dateRangeIndicator_dateRangeIcon)
        if (it.second == 0L && it.first == 0L) {
            dateRangeText.text = ""
            dateRangeText.visibility = GONE
            dateRangeIcon.setImageResource(R.drawable.ic_calendar)
            dateRangeIcon.setOnClickListener { showDatePicker() }
        } else {
            dateRangeText.text = DateRangeText(
                DateCalendar(it.first),
                DateCalendar(it.second)
            ).value()
            dateRangeText.visibility = VISIBLE
            dateRangeIcon.setImageResource(R.drawable.ic_cross)
            dateRangeIcon.setOnClickListener { viewModel.clearDateRange() }
        }
        dateRangeText.setOnClickListener { showDatePicker() }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(R.string.Select_date_range)
            .setSelection(
                Pair(
                    MaterialDatePicker.todayInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build()
        datePicker.addOnPositiveButtonClickListener {
            viewModel.selectDateRange(it.first, it.second)
        }
        datePicker.show(childFragmentManager, null)
    }

    override fun onPause() {
        super.onPause()
        lastCameraPosition = map?.cameraPosition
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
        clusterManager.setOnClusterItemInfoWindowClickListener {
            toJotFragment(it.jot)
        }
        clusterManager.setOnClusterClickListener {
            toJotsFragment(it.items.map { item -> item.jot.id() }.toLongArray())
            true
        }
        viewModel.jotsLiveData().observe(viewLifecycleOwner) {
            loadUpMapMarkers(map, clusterManager, it)
        }
        viewModel.loadJots()
    }


    private fun loadUpMapMarkers(
        map: GoogleMap,
        clusterManager: ClusterManager<JotMapItem>,
        jots: List<Jot>
    ) {
        val latLngBounds = LatLngBounds.Builder()
        var haveLocation = false
        map.clear()
        clusterManager.clearItems()
        jots.filter {
            !it.location().contentEquals(doubleArrayOf(Double.MIN_VALUE, Double.MIN_VALUE)) &&
                !it.location().contentEquals(doubleArrayOf(0.0, 0.0))
        }.forEach { jot ->
            val position = LatLng(jot.location()[1], jot.location()[0])
            clusterManager.addItem(JotMapItem(jot))
            latLngBounds.include(position)
            haveLocation = true
        }
        if (lastCameraPosition != null) {
            map.moveCamera(CameraUpdateFactory.newCameraPosition(lastCameraPosition))
        } else {
            if (haveLocation) {
                map.setMaxZoomPreference(15f)
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 200))
            } else {
                map.animateCamera(CameraUpdateFactory.zoomTo(10f))
            }
        }
    }

    private fun toJotFragment(jot: Jot) {
        findNavController().navigate(
            JotsMapFragmentDirections.actionJotsMapFragmentToJotFragment2(jot.id())
        )
    }

    private fun toJotsFragment(jotIds: LongArray) {
        findNavController().navigate(
            JotsMapFragmentDirections.actionJotsMapFragmentToJotsFragment(jotIds)
        )
    }
}