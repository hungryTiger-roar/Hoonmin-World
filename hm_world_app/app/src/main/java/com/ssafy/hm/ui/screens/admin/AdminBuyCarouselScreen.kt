package com.ssafy.hm.ui.screens.admin

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ssafy.hm.ui.state.UploadViewModel
import com.ssafy.hm.ui.state.UploadViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBuyCarouselScreen(
    onBack: () -> Unit,
    onUploaded: () -> Unit,
    uploadVm: UploadViewModel = viewModel(factory = UploadViewModelFactory())
) {
    val context = LocalContext.current
    val state by uploadVm.state.collectAsState()
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraLaunch by remember { mutableStateOf(false) }
    var cameraFile by remember { mutableStateOf<File?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedUri = cameraFile?.let { file ->
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingCameraLaunch) {
            pendingCameraLaunch = false
            launchCamera(context, onFileReady = { file ->
                cameraFile = file
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                takePictureLauncher.launch(uri)
            })
        } else if (!granted) {
            Toast.makeText(context, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(state.message, state.error) {
        state.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            onUploaded()
            onBack()
            uploadVm.clearResult()
        }
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            uploadVm.clearResult()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("상품 캐러셀 관리") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = selectedUri,
                contentDescription = "선택된 이미지",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Button(
                onClick = {
                    val granted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                    if (granted) {
                        launchCamera(context, onFileReady = { file ->
                            cameraFile = file
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )
                            takePictureLauncher.launch(uri)
                        })
                    } else {
                        pendingCameraLaunch = true
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("사진 촬영")
            }
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("갤러리에서 선택")
            }
            Button(
                onClick = {
                    val uri = selectedUri
                    if (uri == null) {
                        Toast.makeText(context, "먼저 이미지를 선택하세요.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val part = createMultipart(context, uri)
                    uploadVm.uploadAndInsertBuyImage(part)
                },
                enabled = !state.loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.loading) "업로드 중..." else "업로드")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("/upload에 multipart 필드 'upload_file'로 업로드됩니다.")
        }
    }
}

private fun launchCamera(
    context: Context,
    onFileReady: (File) -> Unit
) {
    val dir = File(context.cacheDir, "images")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    val file = File.createTempFile("camera_", ".jpg", dir)
    onFileReady(file)
}

private fun createMultipart(context: Context, uri: Uri): MultipartBody.Part {
    val resolver = context.contentResolver
    val fileName = getFileName(resolver, uri)
    val mimeType = resolver.getType(uri) ?: "image/*"
    val file = copyUriToFile(context, uri, fileName)
    val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("upload_file", file.name, requestBody)
}

private fun getFileName(resolver: ContentResolver, uri: Uri): String {
    val nameFromUri = resolver.query(uri, null, null, null, null)?.use { cursor ->
        val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (idx != -1 && cursor.moveToFirst()) {
            cursor.getString(idx)
        } else {
            null
        }
    }
    if (!nameFromUri.isNullOrBlank()) return nameFromUri

    val extension = MimeTypeMap.getSingleton()
        .getExtensionFromMimeType(resolver.getType(uri))
        ?.let { ".$it" }
        ?: ".jpg"
    return "upload_${System.currentTimeMillis()}$extension"
}

private fun copyUriToFile(context: Context, uri: Uri, fileName: String): File {
    val dir = File(context.cacheDir, "uploads")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    val file = File(dir, fileName)
    context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    return file
}
