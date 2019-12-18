package com.larryhsiao.nyx.view.diary

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Service.BIND_AUTO_CREATE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import co.lujun.androidtagview.TagView
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.azure.TranslatedStrings
import com.larryhsiao.nyx.diary.Diary
import com.larryhsiao.nyx.tag.Tag
import com.larryhsiao.nyx.view.diary.attachment.FindAttachmentIntent
import com.larryhsiao.nyx.view.diary.attachment.ImageFactory
import com.larryhsiao.nyx.view.diary.attachment.ResultProcessor
import com.larryhsiao.nyx.view.diary.attachment.ViewAttachmentIntent
import com.larryhsiao.nyx.view.diary.viewmodel.AddressViewModel
import com.larryhsiao.nyx.view.diary.viewmodel.CalendarViewModel
import com.larryhsiao.nyx.view.tag.viewmodel.TagAttachmentVM
import com.larryhsiao.nyx.view.tag.viewmodel.TagListVM
import com.larryhsiao.nyx.view.weather.WeatherViewModel
import com.silverhetch.aura.AuraFragment
import com.silverhetch.aura.location.LocationService
import com.silverhetch.aura.view.fab.FabBehavior
import com.silverhetch.clotho.geo.GeoUri
import com.silverhetch.clotho.time.ToUTCTimestamp
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.page_diary.*
import kotlinx.android.synthetic.main.page_diary.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Page for creating new diary.
 */
class NewDiaryFragment : AuraFragment(), ServiceConnection {
    companion object {
        private const val REQUEST_CODE_ADD_IMAGE = 1000
    }

    private lateinit var addressVM: AddressViewModel
    private lateinit var calendarVM: CalendarViewModel
    private lateinit var tagViewVM: TagAttachmentVM
    private lateinit var tagVM: TagListVM
    private lateinit var weatherVM: WeatherViewModel
    private var calendar = Calendar.getInstance()
    private val tagInputWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            tagVM.loadUpTags(s?.toString() ?: "")
        }

        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
            /* Leave it empty */
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            /* Leave it empty */
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstnceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.page_diary, container, false)
        ViewModelProviders.of(this).apply {
            addressVM = get(AddressViewModel::class.java)
            tagVM = get(TagListVM::class.java)
            calendarVM = get(CalendarViewModel::class.java)
            tagViewVM = get(TagAttachmentVM::class.java)
            weatherVM = get(WeatherViewModel::class.java)
        }
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
                            tagViewVM.attachToDiary(it.id()).observe(
                                this@NewDiaryFragment, Observer<List<Tag>> {
                                    activity?.onBackPressed()
                                })
                            weatherVM.attachToDiary(it.id())
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
        rootView.newDiary_inputTag.addTextChangedListener(tagInputWatcher)
        rootView.newDiary_newtagButton.setOnClickListener {
            createTagByInput()
        }

        rootView.newDiary_tag.setOnTagClickListener(object :
            TagView.OnTagClickListener {
            override fun onSelectedTagDrag(position: Int, text: String?) {
                /*Leave it empty*/
            }

            override fun onTagLongClick(position: Int, text: String?) {
                /*Leave it empty*/
            }

            override fun onTagClick(position: Int, text: String?) {
                if (text.isNullOrEmpty()) {
                    return
                }
                AlertDialog.Builder(context)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.delete)
                    .setPositiveButton(R.string.confirm) { _, _ ->
                        tagViewVM.removeTag(text)
                        rootView.newDiary_tag.removeTag(position)
                    }
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .show()
            }

            override fun onTagCrossClick(position: Int) {
                /*Leave it empty*/
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
        addressVM.address().observe(this, Observer {
            newDiary_locationIndicator.text = it
        })
        tagVM.tags().observe(this, Observer { tags ->
            if (tags.isNotEmpty()) {
                rootView.newDiary_inputTag.apply {
                    setAdapter(ArrayAdapter<String>(
                        rootView.context,
                        R.layout.item_tag,
                        R.id.tag_title,
                        Array(tags.size) {
                            tags[it].title()
                        }
                    ))
                }
            }
        })
        weatherVM.weather().observe(this, Observer {
            it.takeIf {
                it.iconUrl().isNotEmpty()
            }?.let {
                Picasso.get()
                    .load(it.iconUrl())
                    .placeholder(CircularProgressDrawable(rootView.context).apply {
                        setStyle(CircularProgressDrawable.LARGE)
                    })
                    .into(rootView.newDiary_weatherIcon)
                rootView.newDiary_weatherIcon.visibility = View.VISIBLE
            }
        })
        return rootView
    }

    override fun onResume() {
        super.onResume()
        context?.bindService(
            Intent(context, LocationService::class.java),
            this,
            BIND_AUTO_CREATE
        )
    }

    override fun onPause() {
        super.onPause()
        context?.unbindService(this)
    }

    private fun haveGeoUri(): Boolean {
        for (key in newDiary_imageGrid.sources().keys) {
            if (key.startsWith("geo:")) {
                return true
            }
        }
        return false
    }

    private fun createTagByInput() {
        val tagName = newDiary_inputTag.text.toString()
        if (!newDiary_tag.tags.contains(tagName) && !tagName.isEmpty()) {
            tagViewVM.preferTag(tagName)
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
        if (requestCode == REQUEST_CODE_ADD_IMAGE &&
            resultCode == RESULT_OK &&
            data != null
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
            if (it.toString().startsWith("geo:")) {
                addressVM.load(it.toString())
            }

            if (it.toString().startsWith("content:") || it.toString().startsWith(
                    "file:"
                )
            ) {
                FirebaseVision.getInstance().getOnDeviceImageLabeler(
                    FirebaseVisionOnDeviceImageLabelerOptions.Builder()
                        .setConfidenceThreshold(0.95f)
                        .build()
                ).processImage(FirebaseVisionImage.fromFilePath(context, it))
                    .addOnSuccessListener { labels ->
                        TranslatedStrings(
                            context,
                            labels.map { it.text }
                        ).value().observe(this, Observer {
                            it.forEach {
                                tagViewVM.preferTag(it)
                                newDiary_tag.addTag(it)
                            }
                        })
                    }
            }
        }.proceed(data)
    }

    private fun updateDateIndicator(view: View) {
        view.newDiary_date.text =
            SimpleDateFormat.getDateInstance()
                .format(Date().apply { time = calendar.timeInMillis })
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        // do nothing
    }

    private fun shouldPreloadLcoation(): Boolean {
        return context?.let { context ->
            PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(
                    getString(R.string.prefKey_preloadLocation),
                    false
                )
        } ?: false
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (service is LocationService.Binder) {
            service.location().observe(this, Observer {
                if (!haveGeoUri() && shouldPreloadLcoation()) {
                    newDiary_imageGrid.addImage(
                        ImageFactory(
                            context!!,
                            Uri.parse(
                                GeoUri(
                                    it.longitude,
                                    it.latitude
                                ).value().toASCIIString()
                            )
                        ).value()
                    )
                    weatherVM.byGeo(it.latitude, it.longitude)
                }
            })
        }
    }
}