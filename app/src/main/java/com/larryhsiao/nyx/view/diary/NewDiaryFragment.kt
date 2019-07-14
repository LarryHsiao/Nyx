package com.larryhsiao.nyx.view.diary

import android.Manifest
import android.Manifest.permission.*
import android.Manifest.permission_group.*
import android.app.Activity.*
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.diary.Diary
import com.larryhsiao.nyx.media.storage.NewMediaFile
import com.larryhsiao.nyx.view.diary.image.FindImageIntent
import com.larryhsiao.nyx.view.diary.image.ResultUri
import com.larryhsiao.nyx.view.diary.viewmodel.CalendarViewModel
import com.silverhetch.aura.AuraFragment
import com.silverhetch.aura.media.BitmapStream
import com.silverhetch.aura.view.fab.FabBehavior
import com.silverhetch.aura.view.images.CRImage
import com.silverhetch.aura.view.images.ImageActivity
import com.silverhetch.clotho.file.ToFile
import com.silverhetch.clotho.time.ToUTCTimestamp
import kotlinx.android.synthetic.main.page_diary.*
import kotlinx.android.synthetic.main.page_diary.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Page for creating new diary.
 */
class NewDiaryFragment : AuraFragment() {
    companion object {
        private const val REQUEST_CODE_ADD_IMAGE = 1000
    }

    private lateinit var viewModel: CalendarViewModel
    private var calendar = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.page_diary, container, false)
        viewModel =
            ViewModelProviders.of(this).get(CalendarViewModel::class.java)
        attachFab(
            object : FabBehavior {
                override fun onClick() {
                    val title = newDiary_newDiaryContent.text.toString()
                    if (title.isNotEmpty()) {
                        viewModel.newDiary(
                            title,
                            ToUTCTimestamp(calendar.timeInMillis).value(),
                            newDiary_imageGrid.sources().keys.toList()
                        ).observe(this@NewDiaryFragment, Observer<Diary> {
                            activity?.onBackPressed()
                        })
                    } else {
                        Toast.makeText(
                            inflater.context,
                            R.string.title_should_not_empty,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun icon(): Int {
                    return R.drawable.ic_save
                }
            }
        )

        rootView.newDiary_date.setOnClickListener {
            DatePickerDialog(it.context)
                .apply {
                    setOnDateSetListener { _, year, month, dayOfMonth ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        updateDateIndicator(rootView)
                    }
                }.show()
        }
        updateDateIndicator(rootView)

        rootView.newDiary_imageGrid.initImages(listOf(), true)
        rootView.newDiary_imageGrid.setCallback { item, isAddingButton ->
            if (isAddingButton) {
                requestPermissionsByObj(arrayOf(READ_EXTERNAL_STORAGE))
            } else {
                startActivity(
                    ImageActivity.newIntent(
                        rootView.context,
                        item.id()
                    )
                )
            }
        }
        return rootView
    }

    override fun onPermissionGranted() {
        super.onPermissionGranted()

        startActivityForResult(
            FindImageIntent(context!!).value(),
            REQUEST_CODE_ADD_IMAGE
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_IMAGE
            && resultCode == RESULT_OK
            && data != null
        ) {
            try {
                newDiary_imageGrid.addImage(
                    CRImage(
                        newDiary_imageGrid.context,
                        ResultUri(
                            newDiary_imageGrid.context,
                            data
                        ).value()
                    )
                )
            } catch (e: Exception) {
                Toast.makeText(
                    newDiary_imageGrid.context,
                    R.string.unsupported_image_format,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateDateIndicator(view: View) {
        view.newDiary_date.text =
            SimpleDateFormat.getDateInstance()
                .format(Date().apply { time = calendar.timeInMillis })
    }
}