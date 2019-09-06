package com.larryhsiao.nyx.view.diary

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.lujun.androidtagview.TagView
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.diary.Diary
import com.larryhsiao.nyx.tag.Tag
import com.larryhsiao.nyx.view.diary.attachment.FindAttachmentIntent
import com.larryhsiao.nyx.view.diary.attachment.ImageFactory
import com.larryhsiao.nyx.view.diary.attachment.ResultProcessor
import com.larryhsiao.nyx.view.diary.attachment.ViewAttachmentIntent
import com.larryhsiao.nyx.view.diary.viewmodel.CalendarViewModel
import com.larryhsiao.nyx.view.tag.viewmodel.NewDiaryTagViewModel
import com.silverhetch.aura.AuraFragment
import com.silverhetch.aura.view.fab.FabBehavior
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

    private lateinit var calendarVM: CalendarViewModel
    private lateinit var newDiaryTagViewVM: NewDiaryTagViewModel
    private var calendar = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.page_diary, container, false)
        calendarVM =
            ViewModelProviders.of(this).get(CalendarViewModel::class.java)
        newDiaryTagViewVM = ViewModelProviders.of(this).get(NewDiaryTagViewModel::class.java)
        attachFab(
            object : FabBehavior {
                override fun onClick() {
                    val title = newDiary_newDiaryContent.text.toString()
                    if (title.isNotEmpty()) {
                        calendarVM.newDiary(
                            title,
                            ToUTCTimestamp(calendar.timeInMillis).value(),
                            newDiary_imageGrid.sources().keys.toList()
                        ).observe(this@NewDiaryFragment, Observer<Diary> {
                            newDiaryTagViewVM.attachToDiary(it.id()).observe(
                                this@NewDiaryFragment, Observer<List<Tag>> {
                                    activity?.onBackPressed()
                                })
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
                    ViewAttachmentIntent(
                        rootView.context,
                        Uri.parse(item.id())
                    ).value()
                )
            }
        }
        rootView.newDiary_newtagButton.setOnClickListener {
            createTagByInput()
        }

        rootView.newDiary_tag.setOnTagClickListener(object : TagView.OnTagClickListener {
            override fun onSelectedTagDrag(position: Int, text: String?) {
            }

            override fun onTagLongClick(position: Int, text: String?) {
            }

            override fun onTagClick(position: Int, text: String?) {
                if (text.isNullOrEmpty()) {
                    return
                }
                AlertDialog.Builder(context)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.delete)
                    .setPositiveButton(R.string.confirm) { _, _ ->
                        newDiaryTagViewVM.removeTag(text)
                        rootView.newDiary_tag.removeTag(position)
                    }
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .show()
            }

            override fun onTagCrossClick(position: Int) {
            }
        })
        rootView.newDiary_inputTag.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    createTagByInput()
                    true
                }
                else -> {
                    false
                }
            }
        }
        return rootView
    }

    private fun createTagByInput() {
        val tagName = newDiary_inputTag.text.toString()
        if (!newDiary_tag.tags.contains(tagName) && !tagName.isEmpty()) {
            newDiaryTagViewVM.preferTag(tagName)
            newDiary_tag.addTag(tagName)
        }
    }

    override fun onPermissionGranted() {
        super.onPermissionGranted()

        context?.also { context ->
            startActivityForResult(
                FindAttachmentIntent(context).value(),
                REQUEST_CODE_ADD_IMAGE
            )
        }
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
                context?.also { context ->
                    handlingResult(context, data)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    newDiary_imageGrid.context,
                    R.string.unsupported_image_format,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handlingResult(context: Context, data: Intent) {
        ResultProcessor(context) {
            newDiary_imageGrid.addImage(
                ImageFactory(
                    context,
                    it
                ).value()
            )
        }.proceed(data)
    }

    private fun updateDateIndicator(view: View) {
        view.newDiary_date.text =
            SimpleDateFormat.getDateInstance()
                .format(Date().apply { time = calendar.timeInMillis })
    }
}