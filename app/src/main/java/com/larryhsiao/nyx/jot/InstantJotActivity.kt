package com.larryhsiao.nyx.jot

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.larryhsiao.nyx.BuildConfig
import com.larryhsiao.nyx.JotApplication
import com.larryhsiao.nyx.ViewModelFactory
import com.larryhsiao.nyx.old.attachments.TempAttachmentFile
import com.larryhsiao.nyx.old.base.JotActivity

/**
 * Activity that create a new jot by image captured by third-party apps. Then close activity itself.
 */
class InstantJotActivity : JotActivity() {
    private val model by lazy {
        ViewModelProvider(
            this,
            ViewModelFactory(application as JotApplication)
        ).get(JotsCalendarViewModel::class.java)
    }
    private val photoTempFile by lazy {
        TempAttachmentFile(
            this,
            TEMP_FILE_PHOTO_CAPTURE
        ).value()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newJotByCamera(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_CAPTURE_PHOTO){
            if (photoTempFile.exists()) {
                val tempFile = TempAttachmentFile(
                    this,
                    "" + System.currentTimeMillis() + ".jpg"
                ).value()
                photoTempFile.renameTo(tempFile)
                model.newJotsByImage(
                    FileProvider.getUriForFile(
                        this,
                        BuildConfig.FILE_PROVIDER_AUTHORITY,
                        tempFile
                    )
                )
            }
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION_CAMERA_FOR_NEW_JOT &&
            grantResults[0] == PERMISSION_GRANTED) {
            newJotByCamera(this)
        } else {
            finish()
        }
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
            if (intent.resolveActivity(packageManager) != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
                    this,
                    BuildConfig.FILE_PROVIDER_AUTHORITY,
                    photoTempFile
                ))
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_PHOTO)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val REQUEST_CODE_CAPTURE_PHOTO = 1000
        private const val REQUEST_CODE_PERMISSION_CAMERA_FOR_NEW_JOT = 1001

        /**
         *  Use fixed file path as camera output.
         */
        private const val TEMP_FILE_PHOTO_CAPTURE = "TEMP_FILE_PHOTO_CAPTURE"
    }
}