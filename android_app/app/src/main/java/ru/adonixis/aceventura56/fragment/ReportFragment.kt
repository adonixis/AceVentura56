package ru.adonixis.aceventura56.fragment

import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_report.*
import ru.adonixis.aceventura56.R
import ru.adonixis.aceventura56.activity.MapActivity
import java.io.File
import java.io.IOException

class ReportFragment : Fragment() {
    companion object {
        private const val REQUEST_TAKE_PHOTO = 1
        private const val REQUEST_PICK_IMAGE = 2
        private const val REQUEST_GEO_POSITION = 3
        private const val CAPTURE_IMAGE_FILE_PROVIDER = "ru.adonixis.aceventura56.fileprovider"
    }

    private var filePhoto: File? = null
    private var chosenPictureBitmap: Bitmap? = null
    private var isPhotoExist = false
    private var isGeoPositionExist = false
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_report, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivCamera.setOnClickListener{ takePhoto() }
        tvCamera.setOnClickListener{ takePhoto() }

        ivGallery.setOnClickListener{ selectFromGallery() }
        tvGallery.setOnClickListener{ selectFromGallery() }

        ivGeoPosition.setOnClickListener{ getGeoPosition() }
        tvGeoPosition.setOnClickListener{ getGeoPosition() }
    }

    private fun takePhoto() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        filePhoto = File(File(requireContext().filesDir, "photos"), "image.jpg")
        if (filePhoto!!.exists()) {
            filePhoto!!.delete()
        } else {
            filePhoto!!.parentFile.mkdirs()
        }
        val imageUri: Uri = FileProvider.getUriForFile(
            requireContext(),
            CAPTURE_IMAGE_FILE_PROVIDER,
            filePhoto!!
        )
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            takePhotoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            val clip = ClipData.newUri(requireContext().contentResolver, "A photo", imageUri)
            takePhotoIntent.clipData = clip
            takePhotoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO)
    }

    private fun selectFromGallery() {
        val pickImageIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickImageIntent.type = "image/*"
        startActivityForResult(pickImageIntent, REQUEST_PICK_IMAGE)
    }

    private fun getGeoPosition() {
        val intent = Intent(activity, MapActivity::class.java)
        startActivityForResult(intent, REQUEST_GEO_POSITION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_IMAGE -> if (resultCode == RESULT_OK && data != null && data.data != null) {
                val uri = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                    ivPet.setImageBitmap(bitmap)
                    chosenPictureBitmap = bitmap
                    isPhotoExist = true
                    if (isGeoPositionExist) {
                        btnSend.isEnabled = true
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            REQUEST_TAKE_PHOTO -> if (resultCode == RESULT_OK) {
                val uri = FileProvider.getUriForFile(requireContext(), CAPTURE_IMAGE_FILE_PROVIDER, filePhoto!!)
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                    ivPet.setImageBitmap(bitmap)
                    chosenPictureBitmap = bitmap
                    isPhotoExist = true
                    if (isGeoPositionExist) {
                        btnSend.isEnabled = true
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            REQUEST_GEO_POSITION -> if (resultCode == RESULT_OK && data != null) {
                latitude = data.getDoubleExtra("latitude", 0.0)
                longitude = data.getDoubleExtra("longitude", 0.0)
                tvCoords.text = "Координаты: широта - " + String.format("%.3f", latitude).toString() + ", долгота -  " + String.format("%.3f", longitude).toString()
                isGeoPositionExist = true
                if (isPhotoExist) {
                    btnSend.isEnabled = true
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}