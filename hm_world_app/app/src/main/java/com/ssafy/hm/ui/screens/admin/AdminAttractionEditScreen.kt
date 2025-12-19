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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ssafy.hm.data.model.Attraction
import com.ssafy.hm.ui.theme.AuroraPurple
import com.ssafy.hm.ui.state.UploadViewModel
import com.ssafy.hm.ui.state.UploadViewModelFactory
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAttractionEditScreen(
    attraction: Attraction?,
    onBack: () -> Unit,
    onSave: (Attraction) -> Unit,
    uploadVm: UploadViewModel = viewModel(factory = UploadViewModelFactory())
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var name by remember { mutableStateOf(attraction?.attName.orEmpty()) }
    var capacity by remember { mutableStateOf(attraction?.attCapacity?.toString().orEmpty()) }
    var comment by remember { mutableStateOf(attraction?.attComment.orEmpty()) }
    var able by remember { mutableStateOf(attraction?.attAble ?: true) }
    var category by remember { mutableStateOf(attraction?.attCategory ?: "스릴") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraLaunch by remember { mutableStateOf(false) }
    var cameraFile by remember { mutableStateOf<File?>(null) }
    var showPicker by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

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

    if (showPicker) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                Button(onClick = {
                    showPicker = false
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
                }) {
                    Text("카메라")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showPicker = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("갤러리")
                }
            },
            title = { Text("이미지 선택") }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(if (attraction == null) "놀이기구 추가" else "놀이기구 수정", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("어트랙션 이름") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    cursorColor = Color.White
                )
            )
            Button(
                onClick = { showPicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AuroraPurple, contentColor = Color.White)
            ) {
                Text("이미지 등록")
            }
            val previewUrl = selectedUri ?: attraction?.attPic ?: DEFAULT_ATTRACTION_IMAGE_URL
            AsyncImage(
                model = previewUrl,
                contentDescription = "미리보기",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            OutlinedTextField(
                value = capacity,
                onValueChange = { capacity = it },
                label = { Text("총 정원 수") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    cursorColor = Color.White
                )
            )
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("코멘트") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    cursorColor = Color.White
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("운영 유무")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(checked = able, onCheckedChange = { able = it })
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (able) "운영 중" else "운영 중단")
            }
            Text("카테고리")
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = category == "스릴", onClick = { category = "스릴" })
                Text("스릴")
                Spacer(modifier = Modifier.width(12.dp))
                RadioButton(selected = category == "가족", onClick = { category = "가족" })
                Text("가족")
                Spacer(modifier = Modifier.width(12.dp))
                RadioButton(selected = category == "어린이", onClick = { category = "어린이" })
                Text("어린이")
            }
            Button(
                onClick = {
                    if (saving) return@Button
                    saving = true
                    scope.launch {
                        try {
                            val cap = capacity.toIntOrNull() ?: 0
                            val link = if (selectedUri != null) {
                                val part = createMultipart(context, selectedUri!!)
                                uploadVm.uploadAndGetLink(part)
                            } else {
                                attraction?.attPic ?: DEFAULT_ATTRACTION_IMAGE_URL
                            }
                            val payload = Attraction(
                                attId = attraction?.attId ?: 0,
                                attName = name,
                                attPic = link,
                                attCapacity = cap,
                                attComment = comment.ifBlank { null },
                                attAble = able,
                                attCategory = category,
                                attTotal = attraction?.attTotal ?: 0
                            )
                            onSave(payload)
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message ?: "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        } finally {
                            saving = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AuroraPurple, contentColor = Color.White)
            ) {
                Text(if (saving) "저장 중..." else "저장")
            }
        }
    }
}

// TODO: pic이 null인 경우 보여줄 기본 이미지 URL을 여기에 넣어주세요.
private const val DEFAULT_ATTRACTION_IMAGE_URL = "https://example.com/default-attraction.jpg"

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
