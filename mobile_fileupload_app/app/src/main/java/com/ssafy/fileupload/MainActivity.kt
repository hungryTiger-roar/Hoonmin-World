package com.ssafy.fileupload

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.ssafy.fileupload.databinding.ActivityMainBinding
import com.ssafy.fileupload.service.UploadService
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.max

private const val TAG = "MainActivity_싸피"
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding( left = systemBars.left, top = systemBars.top, right = systemBars.right, bottom = systemBars.bottom)
            insets
        }

        initEvent()
    }

    private fun initEvent() {
        binding.apply {
            camera.setOnClickListener{
                openCamera()
            }

            upload.setOnClickListener {
                val service = ApplicationClass.retrofit.create(UploadService::class.java)

                lifecycleScope.launch {

                    // name 이 request_param으로 서버에서 읽는 이름.
                    val response = service.uploadFile(MultipartBody.Part.createFormData("upload_file", file.name, file.asRequestBody()))

                    if(response.isSuccessful){
                        response.body()?.let {
                            Log.d(TAG, "response.body()...: ${response.body().toString()}")
                            Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Log.d(TAG, "initEvent - onResponse : Error code ${response.code()}")
                    }
                }
            }
        }
    }

    private lateinit var file: File
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        file = createImageFile()
        //AndroidMenifest에 설정된 URI와 동일한 값으로 설정한다.
        val photoUri = FileProvider.getUriForFile(this, "com.ssafy.fileupload.fileprovider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        requestActivity.launch(intent) //카메라 앱을 실행 한 후 결과를 받기 위해서 launch
    }


    private val requestActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            binding.imageView.setImageURI(Uri.fromFile(file))

            // bitmap으로 변환해서 보여주기..
//            if (Build.VERSION.SDK_INT >= 29) {
//                val source: ImageDecoder.Source = ImageDecoder.createSource(contentResolver, Uri.fromFile(file))
//                val bitmap = ImageDecoder.decodeBitmap(source)
//                imageView.setImageBitmap(bitmap)
//            } else {
//                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))
//                imageView.setImageBitmap(bitmap)
//            }
        }
    }

    private lateinit var currentPhotoPath: String

    // Create an image file name
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path
            currentPhotoPath = absolutePath
        }
    }
}