package com.larryhsiao.nyx.view.diary.map

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.diary.Diary
import com.larryhsiao.nyx.view.diary.DiaryFragment
import com.larryhsiao.nyx.view.diary.DiaryListFragment
import com.larryhsiao.nyx.view.diary.NewDiaryActivity
import com.larryhsiao.nyx.view.diary.viewmodel.LocationViewModel
import com.silverhetch.aura.AuraFragment
import com.silverhetch.aura.view.fab.FabBehavior

/**
 * Fragment that shows diary on map.
 */
class DiaryMapFragment : AuraFragment(), FabBehavior {
    companion object {
        private const val ARG_DATETIME = "ARG_DATETIME"
        private const val ARG_TAG_ID = "ARG_TAG_ID"
        private const val REQUEST_CODE_NEW_DIARY = 1000
        private const val REQUEST_CODE_DIARY = 1001

        /**
         * @param dateTime The specific date in timestamp
         */
        fun newInstance(dateTime: Long = -1L, tagId: Long = -1L): Fragment {
            return DiaryMapFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_DATETIME, dateTime)
                    putLong(ARG_TAG_ID, tagId)
                }
            }
        }
    }

    private lateinit var viewModel: LocationViewModel
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProviders.of(this).get(LocationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FrameLayout(inflater.context).apply {
            id = View.generateViewId()
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
    }

    override fun onResume() {
        super.onResume()
        attachFab(this)
        view?.let { view ->
            childFragmentManager.beginTransaction()
                .replace(view.id, SupportMapFragment.newInstance().apply {
                    this.getMapAsync {
                        map = it
                        loadData()
                    }
                }).commit()
        }
    }

    override fun onPause() {
        super.onPause()
        detachFab()
        view?.let { view ->
            childFragmentManager.findFragmentById(view.id)?.let { mapFragment ->
                childFragmentManager.beginTransaction()
                    .remove(mapFragment)
                    .commit()
            }
        }
    }

    override fun onClick() {
        startActivityForResult(
            Intent(context, NewDiaryActivity::class.java),
            REQUEST_CODE_NEW_DIARY
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_NEW_DIARY) {
            loadData()
        }
        if (requestCode == REQUEST_CODE_DIARY) {
            activity?.onBackPressed()
            loadData()
        }
    }

    private fun loadData() {
        setTitle(getString(R.string.map))
        viewModel.loadUp().observe(this, Observer {
            loadData(it)
        })
    }

    private fun loadData(data: List<Diary>) {
        val context = context ?: return
        ClusterManager<DiaryItem>(context, map).apply {
            map.setOnCameraIdleListener(this)
            map.setOnMarkerClickListener(this)
            map.setOnInfoWindowClickListener(this)

            val latLngBounds = LatLngBounds.builder()
            data.forEach {
                addItem(DiaryItem(it).apply {
                    latLngBounds.include(position)
                })
            }
            map.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    latLngBounds.build(),
                    200
                )
            )

            renderer = DefaultClusterRenderer<DiaryItem>(
                context,
                map,
                this
            ).apply {
                minClusterSize = 2
            }

            setOnClusterClickListener { items ->
                nextPage(
                    DiaryListFragment.newInstance(
                        items.items.map {
                            it.diary().id()
                        }.toLongArray()
                    )
                )
                false
            }
            setOnClusterItemClickListener {
                nextPage(DiaryFragment.newInstance(it.diary().id()))
                false
            }
        }
    }

    override fun icon(): Int {
        return R.drawable.ic_plus
    }
}