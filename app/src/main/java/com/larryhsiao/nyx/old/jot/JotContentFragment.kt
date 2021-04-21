package com.larryhsiao.nyx.old.jot

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.DialogInterface
import android.content.Intent
import android.location.Address
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.larryhsiao.clotho.date.DateCalendar
import com.larryhsiao.clotho.source.ConstSource
import com.larryhsiao.nyx.BuildConfig
import com.larryhsiao.nyx.NyxApplication
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.core.attachments.*
import com.larryhsiao.nyx.core.jots.*
import com.larryhsiao.nyx.core.jots.moods.DefaultMoods
import com.larryhsiao.nyx.core.jots.moods.MergedMoods
import com.larryhsiao.nyx.core.jots.moods.RankedMood
import com.larryhsiao.nyx.core.jots.moods.RankedMoods
import com.larryhsiao.nyx.core.tags.*
import com.larryhsiao.nyx.old.LocationString
import com.larryhsiao.nyx.old.attachments.*
import com.larryhsiao.nyx.old.base.JotFragment
import com.larryhsiao.nyx.old.sync.SyncService
import com.larryhsiao.nyx.old.util.EmbedMapFragment
import com.larryhsiao.nyx.old.util.JpegDateComparator
import com.larryhsiao.nyx.old.util.exif.ExifLocation
import com.linkedin.urls.detection.UrlDetector
import com.linkedin.urls.detection.UrlDetectorOptions
import com.schibstedspain.leku.ADDRESS
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LONGITUDE
import com.schibstedspain.leku.LocationPickerActivity
import com.silverhetch.aura.BackControl
import com.silverhetch.aura.images.exif.ExifAttribute
import com.silverhetch.aura.images.exif.ExifUnixTimeStamp
import com.silverhetch.aura.location.LocationAddress
import com.silverhetch.aura.uri.UriMimeType
import com.silverhetch.aura.view.fab.FabBehavior
import com.silverhetch.aura.view.recyclerview.slider.DotIndicatorDecoration
import com.silverhetch.aura.view.recyclerview.slider.Slider
import kotlinx.android.synthetic.main.page_jot.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors

/**
 * Fragment that shows the Jot content.
 *
 * @todo #0 One click touch template jot with geometry, and some pictures.
 */
class JotContentFragment : JotFragment(), BackControl {
    private val attachmentOnView: MutableList<Uri?> = ArrayList()
    private val mainHandler = Handler()
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private var adapter: AttachmentSliderAdapter? = null
    private var jot: Jot? = null
    override fun onDestroy() {
        super.onDestroy()
        backgroundThread!!.quitSafely()
        backgroundThread!!.interrupt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backgroundThread = HandlerThread("ContentBackground")
        backgroundThread!!.start()
        backgroundHandler = Handler(backgroundThread!!.looper)
        setHasOptionsMenu(true)
        jot = if (arguments != null) {
            Gson().fromJson(
                arguments?.getString(ARG_JOT_JSON, "{}") ?: "{}",
                ConstJot::class.java
            )
        } else {
            ConstJot(
                -1,
                "",
                "",
                System.currentTimeMillis(), doubleArrayOf(Double.MIN_VALUE, Double.MIN_VALUE),
                "",
                1,
                false
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setTitle(getString(R.string.jots))
        val view = inflater.inflate(R.layout.page_jot, container, false)
        adapter = AttachmentSliderAdapter(requireContext()) { clickedView: View, uri: String, longClicked: Boolean ->
            if (longClicked) {
                showProperties(clickedView, Uri.parse(uri))
            } else {
                showContent(clickedView, uri)
            }
        }
        jot_attachment_container.setAdapter(adapter)
        Slider(jot_attachment_container).fire()
        return view
    }

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        jot_date.setOnClickListener(View.OnClickListener { v: View? ->
            val calendar = DateCalendar(jot!!.createdTime(), Calendar.getInstance()).value()
            val dialog = DatePickerDialog(
                view.context, label@
            OnDateSetListener { view1: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                jot = object : WrappedJot(jot!!) {
                    override fun createdTime(): Long {
                        val calendar = Calendar.getInstance()
                        calendar[year, month] = dayOfMonth
                        return calendar.timeInMillis
                    }
                }
                updateDateIndicator()
            },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            )
            dialog.show()
        })
        updateDateIndicator()
        val contentEditText = view.findViewById<EditText>(R.id.jot_content)
        contentEditText.setText(jot!!.content())
        contentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
                // Leave it empty
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                // Leave it empty
            }

            override fun afterTextChanged(s: Editable) {
                jot = object : WrappedJot(jot!!) {
                    override fun content(): String {
                        return s.toString()
                    }
                }
                mainHandler.removeCallbacks(handler)
                if (contentEditText.text.toString().endsWith("\n")) {
                    mainHandler.postDelayed(handler, 1000)
                }
            }

            private val handler = Runnable {

                // @todo #10 Detects too many unusable uri.I
                val detect = UrlDetector(
                    contentEditText.text.toString(), UrlDetectorOptions.Default).detect()
                if (detect.size > 0) {
                    preferEnterUrl(detect[detect.size - 1].toString())
                }
            }
        })
        jot_location.setOnClickListener(View.OnClickListener { v: View? -> pickLocation() })
        val location = Location("Constant")
        location.longitude = jot!!.location()[0]
        location.latitude = jot!!.location()[1]
        updateAddress(location)
        loadEmbedMapByJot()
        attachmentOnView.clear()
        attachmentOnView.addAll(
            QueriedAttachments(AttachmentsByJotId(db, jot!!.id()))
                .value()
                .stream()
                .map { it: Attachment -> Uri.parse(it.uri()) }
                .collect(Collectors.toList())
        )
        if (arguments != null) {
            val attachments: List<String>? = arguments?.getStringArrayList(ARG_ATTACHMENT_URI)
            if (attachments != null) {
                for (uri in attachments) {
                    addAttachmentGrantPermission(Uri.parse(uri))
                }
                updateAttachmentView()
            }
        }
        val tagIcon = view.findViewById<ImageView>(R.id.jot_tagIcon)
        tagIcon.setOnClickListener { v: View ->
            val editText = AutoCompleteTextView(v.context)
            editText.setLines(1)
            editText.maxLines = 1
            editText.inputType = InputType.TYPE_CLASS_TEXT
            editText.setAdapter(ArrayAdapter(
                v.context,
                android.R.layout.simple_dropdown_item_1line,
                QueriedTags(
                    AllTags(db)
                ).value().stream().map { obj: Tag -> obj.title() }.collect(Collectors.toList())
            )
            )
            AlertDialog.Builder(v.context)
                .setTitle(getString(R.string.new_tag))
                .setMessage(getString(R.string.enter_tag_name))
                .setView(editText)
                .setPositiveButton(R.string.confirm) { dialog: DialogInterface?, which: Int ->
                    val preferTagName = editText.text.toString()
                    newChip(preferTagName)
                }
                .setNegativeButton(R.string.cancel, null)
                .create().show()
        }
        for (tag in QueriedTags(TagsByJotId(db, jot!!.id())).value()) {
            val chip = Chip(view.context)
            chip.tag = tag
            chip.text = tag.title()
            chip.setOnClickListener { v: View ->
                AlertDialog.Builder(v.context)
                    .setTitle(tag.title())
                    .setMessage(R.string.delete)
                    .setPositiveButton(R.string.confirm
                    ) { dialog: DialogInterface?, which: Int -> jot_tagGroup.removeView(v) }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }
            jot_tagGroup.addView(chip)
        }
        var mood = jot!!.mood()
        if (mood.isEmpty()) {
            mood = "+"
        }
        jot_mood.text = mood
        jot_mood.setOnClickListener(View.OnClickListener { v: View ->
            val gridView = GridView(v.context)
            gridView.numColumns = 4
            gridView.adapter = MoodAdapter(
                v.context,
                MergedMoods(
                    ConstSource(
                        RankedMoods(db).value()
                            .stream()
                            .map { obj: RankedMood -> obj.mood() }
                            .collect(Collectors.toList<String>())
                    ),
                    DefaultMoods()
                ).value()
            )
            val moodDialog = AlertDialog.Builder(v.context)
                .setTitle(getString(R.string.moods))
                .setNegativeButton(R.string.cancel) { dialog: DialogInterface?, which: Int -> }
                .setView(gridView)
                .show()
            gridView.onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>?, view12: View, position: Int, id: Long ->
                if (position == gridView.adapter.count - 1) { // last custom dialog
                    moodDialog.dismiss()
                    val dialog = newInstance(
                        getString(R.string.moods),
                        REQUEST_CODE_INPUT_CUSTOM_MOOD
                    )
                    dialog.setTargetFragment(this,
                        REQUEST_CODE_INPUT_CUSTOM_MOOD)
                    dialog.show(parentFragmentManager, null)
                    return@OnItemClickListener
                }
                val newMood: String
                newMood = if (position == 0) {
                    "+"
                } else {
                    (view12 as TextView).text.toString()
                }
                jot = object : WrappedJot(jot!!) {
                    override fun mood(): String {
                        return newMood
                    }
                }
                jot_mood.setText(newMood)
                moodDialog.dismiss()
            }
        })
    }

    private fun preferEnterUrl(url: String) {
        Thread {
            try {
                val client = OkHttpClient()
                val res = client.newCall(
                    Request.Builder()
                        .method("HEAD", null)
                        .url(url)
                        .build()
                ).execute()
                if (res.code() in 200..299) {
                    postAddAttachment(url)
                }
            } catch (ignore: Exception) {
            }
        }.start()
    }

    private fun postAddAttachment(url: String) {
        view?.post {
            addAttachment(
                Uri.parse(url)
            )
            updateAttachmentView()
        }
    }

    private fun newChip(preferTagName: String) {
        var tag: Tag? = null
        for (searched in QueriedTags(
            TagsByKeyword(db, preferTagName)).value()) {
            if (searched.title() == preferTagName) {
                tag = searched
                break
            }
        }
        if (tag == null) {
            tag = NewTag(db, preferTagName).value()
        }
        val tagChip = Chip(requireContext())
        tagChip.text = tag.title()
        tagChip.setLines(1)
        tagChip.maxLines = 1
        tagChip.tag = tag
        tagChip.setOnClickListener { v1: View? -> tagChipClicked(tagChip) }
        jot_tagGroup.addView(tagChip)
    }

    override fun onResume() {
        super.onResume()
        attachFab(object : FabBehavior {
            override fun icon(): Int {
                return R.drawable.ic_save
            }

            override fun onClick() {
                save()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        detachFab()
    }

    private fun updateAddress(location: Location) {
        backgroundHandler!!.post {
            val value = LocationString(
                LocationAddress(jot_location.context, location).value()
            ).value()
            jot_location.post { jot_location.text = value }
        }
    }

    private fun pickLocation() {
        val build = LocationPickerActivity.Builder()
        if (!Arrays.equals(jot!!.location(), doubleArrayOf(Double.MIN_VALUE, Double.MIN_VALUE))
            && !Arrays.equals(jot!!.location(), doubleArrayOf(0.0, 0.0))) {
            build.withLocation(jot!!.location()[1], jot!!.location()[0])
        }
        startActivityForResult(
            build.build(requireContext()),
            REQUEST_CODE_LOCATION_PICKER
        )
    }

    private fun tagChipClicked(tagChip: Chip) {
        AlertDialog.Builder(requireContext())
            .setTitle(tagChip.text.toString())
            .setAdapter(ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1, arrayOf(
                getString(R.string.delete)
            ))) { dialog1: DialogInterface?, which1: Int -> onTagOptionClicked(tagChip, which1) }
            .show()
    }

    private fun onTagOptionClicked(tagChip: Chip, which1: Int) {
        if (which1 == 0) {
            AlertDialog.Builder(requireContext())
                .setTitle(tagChip.text.toString())
                .setMessage(getString(R.string.delete))
                .setPositiveButton(R.string.confirm
                ) { dialog2: DialogInterface?, which2: Int -> jot_tagGroup.removeView(tagChip) }.setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun updateDateIndicator() {
        jot_date.text = SimpleDateFormat.getDateInstance()
            .format(Date(jot!!.createdTime()))
    }

    override fun onCreateOptionsMenu(
        menu: Menu, inflater: MenuInflater
    ) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.jot_edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuItem_delete) {
            preferDelete()
        }
        return false
    }

    private fun save() {
        jot = PostedJot(db, jot!!).value()
        saveAttachment()
        saveTag()
        SyncService.enqueue(requireContext())
        val intent = Intent()
        intent.data = Uri.parse(
            JotUri(BuildConfig.URI_HOST, jot!!).value().toASCIIString())
        sendResult(
            requireArguments().getInt(ARG_REQUEST_CODE, 0),
            Activity.RESULT_OK,
            intent
        )
    }

    private fun saveTag() {
        val dbTags: MutableMap<Long, Tag> = QueriedTags(TagsByJotId(db, jot!!.id()))
            .value()
            .stream()
            .collect(Collectors.toMap({ obj: Tag -> obj.id() }) { tag: Tag? -> tag })
        for (i in 0 until jot_tagGroup.childCount) {
            val tagOnView = jot_tagGroup.getChildAt(i).tag as Tag
            if (!dbTags.containsKey(tagOnView.id())) {
                // update JOT TAG
                NewJotTag(
                    db,
                    ConstSource(jot!!.id()),
                    ConstSource(tagOnView.id())
                ).fire()
            } else {
                dbTags.remove(tagOnView.id())
            }
        }
        dbTags.forEach { (aLong: Long?, tag: Tag) -> JotTagRemoval(db, jot!!.id(), tag.id()).fire() }
    }

    private fun saveAttachment() {
        val dbAttachments = QueriedAttachments(
            AttachmentsByJotId(db, jot!!.id())
        ).value().toMutableList()
        for (uri in attachmentOnView) {
            var hasItem = false
            val existOnView: MutableList<Attachment> = ArrayList()
            for (dbAttachment in dbAttachments) {
                if (dbAttachment.uri() == uri.toString()) {
                    hasItem = true
                    existOnView.add(dbAttachment)
                }
            }
            dbAttachments.removeAll(existOnView)
            if (!hasItem) {
                NewAttachment(db, uri.toString(), jot!!.id()).value()
            }
        }
        for (attachment in dbAttachments) {
            RemovalAttachment(db, attachment.id()).fire()
        }
    }

    private fun deleteFlow() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete)
            .setPositiveButton(R.string.confirm) { dialog: DialogInterface?, which: Int ->
                deleteTempAttachments()
                RemovalAttachmentByJotId(db, jot!!.id()).fire()
                JotRemoval(db, jot!!.id()).fire()
                parentFragmentManager.popBackStack()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun discardFlow() {
        if (jot!!.id() <= 0) {
            try {
                JotRemoval(db, jot!!.id()).fire()
            } catch (e: Exception) {
                // make sure not have actually inserted into db
                e.printStackTrace()
            }
        }
        if (jot is WrappedJot) { // modified
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.discard)
                .setPositiveButton(R.string.confirm) { dialog: DialogInterface?, which: Int ->
                    deleteTempAttachments()
                    parentFragmentManager.popBackStack()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        } else {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LOCATION_PICKER && resultCode == Activity.RESULT_OK && data != null) {
            jot = object : WrappedJot(jot!!) {
                override fun location(): DoubleArray {
                    return doubleArrayOf(
                        data.getDoubleExtra(LONGITUDE, 0.0),
                        data.getDoubleExtra(LATITUDE, 0.0)
                    )
                }
            }
            val address = data.getParcelableExtra<Address>(ADDRESS)
            jot_location.text = if (address == null) "" else LocationString(address).value()
            loadEmbedMapByJot()
        } else if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.data != null) {
                addAttachmentGrantPermission(data.data)
            } else {
                val clip = data.clipData
                if (clip != null) {
                    for (i in 0 until clip.itemCount) {
                        addAttachmentGrantPermission(
                            clip.getItemAt(i).uri)
                    }
                }
            }
            updateAttachmentView()
        } else if (requestCode == REQUEST_CODE_INPUT_CUSTOM_MOOD && resultCode == Activity.RESULT_OK && data != null) {
            val newMoodRaw = data.getStringExtra("INPUT_FIELD")
            val newMood: String
            newMood = if (newMoodRaw != null && newMoodRaw.length > 1) {
                newMoodRaw.substring(0, 2)
            } else {
                "+"
            }
            jot_mood.text = newMood
            jot = object : WrappedJot(jot!!) {
                override fun mood(): String {
                    return newMood
                }
            }
        } else if (requestCode == REQUEST_CODE_ATTACHMENT_DIALOG &&
            data != null) {
            val uris = data.getParcelableArrayListExtra<Uri>("ARG_ATTACHMENT_URI")
                ?: return
            attachmentOnView.clear()
            for (uri in uris) {
                addAttachment(uri)
            }
            updateAttachmentView()
        } else if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                takeTempPhoto()
            }
        }
    }

    private fun takeTempPhoto() {
        val fileNameByTime = TempAttachmentFile(
            requireContext(),
            "" + System.currentTimeMillis() + ".jpg"
        ).value()
        TempAttachmentFile(requireContext(), TEMP_FILE_NAME)
            .value()
            .renameTo(fileNameByTime)
        addAttachment(
            FileProvider.getUriForFile(
                requireContext(),
                NyxApplication.FILE_PROVIDER_AUTHORITY!!,
                fileNameByTime
            )
        )
        updateAttachmentView()
    }

    private fun deleteTempAttachments() {
        for (uri in attachmentOnView) {
            if (uri.toString().startsWith(NyxApplication.URI_FILE_TEMP_PROVIDER!!)) {
                TempAttachmentFile(
                    requireContext(),
                    uri.toString().replace(NyxApplication.URI_FILE_TEMP_PROVIDER, "")
                ).value().delete()
            }
        }
    }

    private fun updateAttachmentView() {
        val newAttachment = requireView().findViewById<TextView>(R.id.jot_attachment_new)
        newAttachment.visibility = View.VISIBLE
        newAttachment.setOnClickListener { v: View? -> startPicker() }
        val countText = requireView().findViewById<TextView>(R.id.jot_attachment_count)
        countText.visibility = View.VISIBLE
        countText.text = attachmentOnView.size.toString() + ""
        updateAttachmentViewByMimeType()
    }

    private fun updateAttachmentViewByMimeType() {
        adapter!!.renewItems(attachmentOnView.stream()
            .map { obj: Uri? -> obj.toString() }
            .collect(Collectors.toList()))
        for (i in 0 until jot_attachment_container!!.itemDecorationCount) {
            val decoration = jot_attachment_container!!.getItemDecorationAt(i)
            if (decoration is DotIndicatorDecoration) {
                decoration.attachTo(jot_attachment_container!!)
            }
        }
    }

    private fun showProperties(view: View, uri: Uri) {
        val popup = PopupMenu(view.context, view)
        popup.menu.add(R.string.delete)
            .setOnMenuItemClickListener { item: MenuItem? ->
                attachmentOnView.remove(uri)
                updateAttachmentView()
                true
            }
        popup.menu
            .add(view.context.getString(R.string.properties))
            .setOnMenuItemClickListener { item: MenuItem? ->
                AttachmentPropertiesDialog(view.context, uri).fire()
                true
            }
        popup.show()
    }

    private fun showContent(view: View, uri: String) {
        if (attachmentOnView.size > 1) {
            browseAttachments(uri)
        } else {
            LaunchAttachment(view.context, uri).fire()
        }
    }

    private fun startPicker() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_attachment)
            .setItems(R.array.newAttachmentMethods) { dialog: DialogInterface?, which: Int ->
                when (which) {
                    0 -> takePicture()
                    1 -> startActivityForResult(
                        AttachmentPickerIntent().value(),
                        REQUEST_CODE_PICK_FILE
                    )
                    else -> startActivityForResult(
                        AttachmentPickerIntent().value(),
                        REQUEST_CODE_PICK_FILE
                    )
                }
            }
            .show()
    }

    private fun takePicture(requestCode: Int = REQUEST_CODE_TAKE_PICTURE) {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(requireContext().packageManager) !=
                null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
                    requireContext(),
                    NyxApplication.FILE_PROVIDER_AUTHORITY!!,
                    TempAttachmentFile(requireContext(), TEMP_FILE_NAME).value()
                ))
                startActivityForResult(intent, requestCode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun browseAttachments(selectedUri: String) {
        val sorted = attachmentOnView
            .stream()
            .sorted(JpegDateComparator(requireContext()))
            .collect(Collectors.toList())
        attachmentOnView.clear()
        attachmentOnView.addAll(sorted)
        val dialog = AttachmentsFragment.newInstance(attachmentOnView, selectedUri)
        dialog.setTargetFragment(this, REQUEST_CODE_ATTACHMENT_DIALOG)
        dialog.show(parentFragmentManager, null)
    }

    private fun addAttachmentGrantPermission(uri: Uri?) {
        try {
            requireContext().contentResolver.takePersistableUriPermission(
                uri!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        addAttachment(uri)
    }

    private fun unsupportedDialog() {
        newInstance(
            REQUEST_CODE_ALERT,
            getString(R.string.not_supported_file)
        ).show(childFragmentManager, null)
    }

    private fun addAttachment(uri: Uri?) {
        if (attachmentOnView.contains(uri)) {
            return
        }
        val mimeType = UriMimeType(requireContext(), uri.toString()).value()
        if (mimeType.startsWith("image")) {
            updateJotWithExif(uri)
            attachmentOnView.add(uri)
        } else if (mimeType.startsWith("video")) {
            attachmentOnView.add(uri)
        } else if (mimeType.startsWith("audio")) {
            attachmentOnView.add(uri)
        } else {
            if (uri.toString().startsWith("http")) {
                attachmentOnView.add(uri)
            } else {
                unsupportedDialog()
            }
        }
    }

    private fun updateJotWithExif(data: Uri?) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(data!!) ?: return
            val exif = ExifInterface(inputStream)
            updateJotLocation(exif)
            updateJotDateByExif(exif)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateJotDateByExif(exif: ExifInterface) {
        val time = ExifUnixTimeStamp(
            ExifAttribute(
                ConstSource(exif),
                ExifInterface.TAG_DATETIME_ORIGINAL
            )
        ).value()

        // invalid time or is a created jot or there are already have attachment there,
        // remains unchanged.
        if (time == -1L || jot?.id() != -1L || attachmentOnView.size > 0) {
            return
        }
        jot = object : WrappedJot(jot!!) {
            override fun createdTime(): Long {
                /*
                 * The picture recorded time is at same timezone of current place.
                 * So minus the current phone timezone offset for the proper GMT time.
                 */
                val tz = TimeZone.getDefault()
                return time - tz.getOffset(Calendar.ZONE_OFFSET.toLong())
            }
        }
        updateDateIndicator()
    }

    private val isLocationSetup: Boolean
        private get() = (!Arrays.equals(jot!!.location(), doubleArrayOf(Double.MIN_VALUE, Double.MIN_VALUE))
            && !Arrays.equals(jot!!.location(), doubleArrayOf(0.0, 0.0)))

    private fun updateJotLocation(exif: ExifInterface) {
        val location = ExifLocation(exif).value()
        if (isLocationSetup ||
            location.longitude == 0.0 && location.latitude == 0.0) {
            return
        }
        updateJotLocation(location)
    }

    private fun updateJotLocation(location: Location) {
        jot = object : WrappedJot(jot!!) {
            override fun location(): DoubleArray {
                return doubleArrayOf(location.longitude, location.latitude)
            }
        }
        updateAddress(location)
        loadEmbedMapByJot()
    }

    private fun loadEmbedMapByJot() {
        val mgr = childFragmentManager
        if (!Arrays.equals(jot!!.location(), doubleArrayOf(Double.MIN_VALUE, Double.MIN_VALUE))
            && !Arrays.equals(jot!!.location(), doubleArrayOf(0.0, 0.0))) {
            mgr.beginTransaction().replace(
                R.id.jot_embedMapContainer,
                EmbedMapFragment
                    .newInstance(jot!!.location()[0], jot!!.location()[1])
            ).commit()
            requireView().findViewById<View>(R.id.jot_embedMapContainer).visibility = View.VISIBLE
        } else {
            val map = mgr.findFragmentById(R.id.jot_embedMapContainer)
            if (map != null) {
                mgr.beginTransaction().remove(map).commit()
            }
            requireView().findViewById<View>(R.id.jot_embedMapContainer).visibility = View.GONE
        }
    }

    override fun onBackPress(): Boolean {
        return if (jot is WrappedJot) {
            discardFlow()
            true
        } else {
            deleteTempAttachments()
            false
        }
    }

    private fun preferDelete() {
        if (jot!!.id() < 0) {
            discardFlow()
        } else {
            deleteFlow()
        }
    }

    companion object {
        private const val REQUEST_CODE_LOCATION_PICKER = 1000
        private const val REQUEST_CODE_PICK_FILE = 1001
        private const val REQUEST_CODE_INPUT_CUSTOM_MOOD = 1002
        private const val REQUEST_CODE_ALERT = 1003
        private const val REQUEST_CODE_ATTACHMENT_DIALOG = 1004
        private const val REQUEST_CODE_TAKE_PICTURE = 1005
        private const val ARG_JOT_JSON = "ARG_JOT"
        private const val ARG_ATTACHMENT_URI = "ARG_ATTACHMENT_URI"
        private const val ARG_REQUEST_CODE = "ARG_REQUEST_CODE"

        /**
         * We use fixed file name for taking picture, make sure to move the previous taken file
         * before taking new one.
         */
        private const val TEMP_FILE_NAME = "JotContentTakePicture.jpg"
        fun newInstance(jot: Jot?, requestCode: Int): Fragment {
            return newInstance(jot, ArrayList(), requestCode)
        }

        @JvmStatic
        @JvmOverloads
        fun newInstance(
            jot: Jot? =
                ConstJot(
                    -1,
                    "",
                    "",
                    System.currentTimeMillis(), doubleArrayOf(Double.MIN_VALUE, Double.MIN_VALUE),
                    "",
                    1,
                    false
                ), uris: ArrayList<String?>? = ArrayList(), requestCode: Int = 0
        ): Fragment {
            val frag: Fragment = JotContentFragment()
            val args = Bundle()
            args.putString(ARG_JOT_JSON, Gson().toJson(jot))
            args.putStringArrayList(ARG_ATTACHMENT_URI, uris)
            args.putInt(ARG_REQUEST_CODE, requestCode)
            frag.arguments = args
            return frag
        }
    }
}