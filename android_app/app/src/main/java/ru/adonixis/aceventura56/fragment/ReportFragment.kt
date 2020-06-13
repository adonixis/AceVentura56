package ru.adonixis.aceventura56.fragment

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ClipData
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_report.*
import ru.adonixis.aceventura56.R
import ru.adonixis.aceventura56.activity.MainActivity
import ru.adonixis.aceventura56.activity.MapActivity
import java.io.File
import java.io.IOException
import java.util.*


class ReportFragment : Fragment() {
    companion object {
        private const val REQUEST_TAKE_PHOTO = 1
        private const val REQUEST_PICK_IMAGE = 2
        private const val REQUEST_GEO_POSITION = 3
        private const val CAPTURE_IMAGE_FILE_PROVIDER = "ru.adonixis.aceventura56.fileprovider"
        private const val TAG = "ReportFragment"
        private const val USER_ID = "userId"
        private const val EMAIL = "email"
        private const val NAME = "name"
    }

    private lateinit var settings: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var filePhoto: File? = null
    private var chosenPictureBitmap: Bitmap? = null
    private var isPhotoExist = false
    private var isGeoPositionExist = false
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var imagesPetsRef: StorageReference
    private lateinit var remoteDB: FirebaseFirestore
    private var filePath: Uri? = null
    private var userId: String = ""
    private var email: String = ""
    private var name: String = ""
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_report, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storage = Firebase.storage
        storageReference = storage.reference
        imagesPetsRef = storageReference.child("images_pets/")
        remoteDB = Firebase.firestore

        settings = PreferenceManager.getDefaultSharedPreferences(activity)
        editor = settings.edit()
        userId = settings.getString(USER_ID, "")!!
        email = settings.getString(EMAIL, "")!!
        name = settings.getString(NAME, "")!!

        ivCamera.setOnClickListener{ takePhoto() }
        tvCamera.setOnClickListener{ takePhoto() }

        ivGallery.setOnClickListener{ selectFromGallery() }
        tvGallery.setOnClickListener{ selectFromGallery() }

        ivGeoPosition.setOnClickListener{ getGeoPosition() }
        tvGeoPosition.setOnClickListener{ getGeoPosition() }

        btnSend.setOnClickListener { uploadImage() }

        progressDialog = ProgressDialog(activity)
        progressDialog!!.isIndeterminate = true
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage(getString(R.string.progress_message_uploading))
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
                filePath = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, filePath)
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
                filePath = FileProvider.getUriForFile(requireContext(), CAPTURE_IMAGE_FILE_PROVIDER, filePhoto!!)
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, filePath)
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

    private fun uploadImage() {
        if (filePath != null) {
            progressDialog!!.show()

            val imageRef = imagesPetsRef.child(UUID.randomUUID().toString() + "-" + System.currentTimeMillis() + ".jpg")

            // adding listeners on upload
            // or failure of image
/*            ref.putFile(filePath!!)
                    .addOnSuccessListener { // Image uploaded successfully
                        // Dismiss dialog
                        progressDialog.dismiss()
                        Toast.makeText(activity, "Image Uploaded!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e -> // Error, Image not uploaded
                        progressDialog.dismiss()
                        Toast.makeText(activity, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnProgressListener { taskSnapshot ->
                        // Progress Listener for loading
                        // percentage on the dialog box
                        val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                        progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                    }*/



            val uploadTask = imageRef.putFile(filePath!!)

            val urlTask: Task<Uri?> = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    progressDialog!!.dismiss()
                    throw task.exception!!
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri: Uri = task.result!!
                    sendRequest(downloadUri, latitude, longitude, email, name, userId)
                } else {
                    progressDialog!!.dismiss()
                }
            }
        }
    }

    private fun sendRequest(
            downloadUri: Uri,
            latitude: Double,
            longitude: Double,
            userEmail: String,
            userName: String,
            userId: String
    ) {
        val calendar = Calendar.getInstance()
        val petRequest = hashMapOf(
                "imageUrl" to downloadUri.toString(),
                "coordLatitude" to latitude,
                "coordLongitude" to longitude,
                "date" to calendar.time,
                "userEmail" to userEmail,
                "userName" to userName,
                "userId" to userId,
                "status" to "Administration"
        )

        remoteDB.collection("petRequests")
                .add(petRequest)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    Toast.makeText(activity, "Запрос успешно отправлен!", Toast.LENGTH_SHORT).show()
                    progressDialog!!.dismiss()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
    }
}