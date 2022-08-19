package com.angcassa.stroyapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.angcassa.stroyapp.R
import com.angcassa.stroyapp.config.ApiConfig
import com.angcassa.stroyapp.config.response.UploadResponse
import com.angcassa.stroyapp.databinding.ActivityUploadBinding
import com.angcassa.stroyapp.viewmodel.AuthPreferences
import com.angcassa.stroyapp.viewmodel.AuthViewModel
import com.angcassa.stroyapp.viewmodel.AuthViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class UploadActivity : AppCompatActivity() {
    private val Context.dataSore: DataStore<Preferences> by preferencesDataStore(name = "Authresponse")
    private lateinit var bind: ActivityUploadBinding
    private lateinit var photoPath: String
    private var getFile: File? = null
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private var token: String = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        bind = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(bind.root)
        showLoading(false)
        val pref = AuthPreferences.getInstance(dataSore)
        val authViewModel =
            ViewModelProvider(this, AuthViewModelFactory(pref))[AuthViewModel::class.java]
        authViewModel.getToken().observe(this)
        {
            token = it.toString()
        }
        getPermission()
        getLocation()

        bind.btUCamera.setOnClickListener {
            cameraStart()
        }

        bind.btUGallery.setOnClickListener {
            galleryStart()
        }

        bind.btUpload.setOnClickListener {
            getLocation()
            if (bind.ImgUStory.drawable == null) {
                Toast.makeText(
                    this@UploadActivity,
                    R.string.gallery,
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (bind.edUDesc.text.toString() != "") {
                showLoading(true)
                if (lat != 0.0) {
                    uploadwGps()
                } else {
                    uploadnoGps()
                }
            } else {
                bind.edUDesc.error = "Harus diisi"
            }
        }

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getLocation()

                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getLocation()
                }
                else -> {
                    // No location access granted.

                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getPermission() {
        if (checkPermission(Manifest.permission.CAMERA)
        ) {
            return
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    private fun getLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                } else {
                    Toast.makeText(
                        this@UploadActivity,
                        R.string.err_Location,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            return
        }
    }

    private fun galleryStart() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val pilih = Intent.createChooser(intent, "Which one")
        openGaleri.launch(pilih)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun cameraStart() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@UploadActivity,
                "com.angcassa.stroyapp",
                it
            )
            photoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            openCamera.launch(intent)
        }
    }

    private val openCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            getFile = File(photoPath)

            val result = BitmapFactory.decodeFile(getFile?.path)
            bind.ImgUStory.setImageBitmap(result)
        }
    }

    private val openGaleri = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            getFile = uriToFile(selectedImg, this@UploadActivity)
            bind.ImgUStory.setImageURI(selectedImg)
        }
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)

        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

        return file
    }

    private fun createTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }


    private fun uploadwGps() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val descriptionText = bind.edUDesc.text.toString()
            val description = descriptionText.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            val client =
                ApiConfig.getApiService()
                    .uploadGps("Bearer $token", imageMultipart, description, lat, lon)
            client.enqueue(object : Callback<UploadResponse> {
                override fun onResponse(
                    call: Call<UploadResponse>,
                    response: Response<UploadResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            showLoading(false)
                            Toast.makeText(
                                this@UploadActivity,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@UploadActivity, MainActivity::class.java))
                            finish()
                        } else {
                            showLoading(false)
                            Toast.makeText(
                                this@UploadActivity,
                                response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(this@UploadActivity, R.string.err_Upload, Toast.LENGTH_SHORT)
                        .show()
                }

            })
        } else {
            Toast.makeText(
                this@UploadActivity,
                R.string.err_Img,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadnoGps() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val descriptionText = bind.edUDesc.text.toString()
            val description = descriptionText.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            val client =
                ApiConfig.getApiService().uploadImage("Bearer $token", imageMultipart, description)
            client.enqueue(object : Callback<UploadResponse> {
                override fun onResponse(
                    call: Call<UploadResponse>,
                    response: Response<UploadResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            showLoading(false)
                            Toast.makeText(
                                this@UploadActivity,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@UploadActivity, MainActivity::class.java))
                            finish()
                        } else {
                            showLoading(false)
                            Toast.makeText(
                                this@UploadActivity,
                                response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(this@UploadActivity, R.string.err_Upload, Toast.LENGTH_SHORT)
                        .show()
                }

            })
        } else {
            Toast.makeText(
                this@UploadActivity,
                R.string.err_Img,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            bind.pbUpload.visibility = View.VISIBLE
            bind.btUpload.isEnabled = false
            bind.btUCamera.isEnabled = false
            bind.btUGallery.isEnabled = false
            bind.edUDesc.isEnabled = false
        } else {
            bind.pbUpload.visibility = View.GONE
            bind.btUpload.isEnabled = true
            bind.btUCamera.isEnabled = true
            bind.btUGallery.isEnabled = true
            bind.edUDesc.isEnabled = true
        }
    }
    private val timeStamp: String = SimpleDateFormat(
        DATE_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())


    companion object {

        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val DATE_FORMAT = "dd-MMM-yyyy"
    }
}