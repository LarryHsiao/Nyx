package com.larryhsiao.nyx.view.diary

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.databinding.PageDiaryBinding
import com.larryhsiao.nyx.diary.Diary
import com.larryhsiao.nyx.view.diary.attachment.FindAttachmentIntent
import com.larryhsiao.nyx.view.diary.attachment.ImageFactory
import com.larryhsiao.nyx.view.diary.attachment.ResultProcessor
import com.larryhsiao.nyx.view.diary.attachment.ViewAttachementIntent
import com.larryhsiao.nyx.view.diary.viewmodel.DiaryViewModel
import com.silverhetch.aura.AuraFragment
import com.silverhetch.aura.view.fab.FabBehavior
import kotlinx.android.synthetic.main.page_diary.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment to show the exist diary.
 */
class DiaryFragment : AuraFragment() {
    companion object {
        private const val ARG_ID = "ARG_ID"
        private const val ARG_EDITABLE = "ARG_EDITABLE"

        private const val REQUEST_CODE_ADD_IMAGE = 1000

        /**
         * Factory method
         */
        fun newInstance(id: Long, editable: Boolean = false): Fragment {
            return DiaryFragment().apply {
                arguments = Bundle().also {
                    it.putLong(ARG_ID, id)
                    it.putBoolean(ARG_EDITABLE, editable)
                }
            }
        }
    }

    private lateinit var viewModel: DiaryViewModel
    private lateinit var editable: MutableLiveData<Boolean>
    private var calendar: Calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.diaryMenu_delete -> {
                AlertDialog.Builder(context)
                    .setMessage(R.string.delete_this_diary)
                    .setPositiveButton(android.R.string.yes) { _, _ ->
                        viewModel.delete().observe(this, Observer {
                            activity?.onBackPressed()
                        })
                    }.setNegativeButton(android.R.string.no) { _, _ ->
                        // leave it empty
                    }
                    .show()
                true
            }
            else -> false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<PageDiaryBinding>(
            inflater,
            R.layout.page_diary,
            container,
            false
        ).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editable = MutableLiveData<Boolean>().also {
            it.value = arguments?.getBoolean(ARG_EDITABLE) ?: false
        }
        viewModel = ViewModelProviders.of(this).get(DiaryViewModel::class.java)
        viewModel.diary().observe(this, Observer<Diary> { diary ->
            val binding = DataBindingUtil.findBinding<PageDiaryBinding>(view)
            binding?.diary = diary
            editable.observe(this, Observer<Boolean> {
                binding?.editable = it
                newDiary_imageGrid.addable(it)
                if (!it) {
                    editableFab()
                } else {
                    saveFab()
                }
            })

            calendar.time = Date().also { it.time = diary.timestamp() }
            val uris = diary.imageUris()
            newDiary_imageGrid.initImages(Array(uris.size) {
                ImageFactory(
                    view.context,
                    uris[it]
                ).value()
            }.toList(), false)
        })
        viewModel.loadUp(arguments?.getLong(ARG_ID) ?: 0L)

        newDiary_date.setOnClickListener {
            DatePickerDialog(it.context)
                .apply {
                    setOnDateSetListener { view, year, month, dayOfMonth ->
                        calendar[Calendar.YEAR] = year
                        calendar[Calendar.MONTH] = month
                        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                        updateDateIndicator()
                    }
                }.show()
        }
        newDiary_imageGrid.initImages(arrayListOf(), false)
        newDiary_imageGrid.setCallback { item, isAddingButton ->
            if (isAddingButton) {
                requestPermissionsByObj(arrayOf(READ_EXTERNAL_STORAGE))
            } else {
                startActivity(
                    ViewAttachementIntent(
                        newDiary_imageGrid.context,
                        Uri.parse(item.id())
                    ).value()
                )
            }
        }
    }

    override fun onPermissionGranted() {
        super.onPermissionGranted()
        context?.also {
            startActivityForResult(
                FindAttachmentIntent(it).value(),
                REQUEST_CODE_ADD_IMAGE
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.diary, menu)
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
                    resultHandling(context, data)
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

    private fun resultHandling(context: Context, data: Intent) {
        ResultProcessor(context) {
            newDiary_imageGrid.addImage(
                ImageFactory(
                    context,
                    it
                ).value()
            )
        }.proceed(data)
    }

    private fun updateDateIndicator() {
        newDiary_date.text = SimpleDateFormat.getDateInstance()
            .format(Date().apply { time = calendar.timeInMillis })
    }

    override fun onBackPress(): Boolean {
        return (editable.value ?: false).also {
            if (it) {
                editable.value = false
            }
        }
    }

    private fun editableFab() {
        attachFab(object : FabBehavior {
            override fun onClick() {
                editable.value = true
                detachFab()
            }

            override fun icon(): Int {
                return R.drawable.ic_pencil
            }
        })
    }

    private fun saveFab() {
        attachFab(object : FabBehavior {
            override fun onClick() {
                val images = newDiary_imageGrid.sources().keys.toTypedArray()
                viewModel.update(
                    newDiary_newDiaryContent.text.toString(),
                    calendar.time.time,
                    Array(images.size) {
                        images[it]
                    }.toList()
                )
                editable.value = false
            }

            override fun icon(): Int {
                return R.drawable.ic_save
            }
        })
    }
}