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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.ssafy.hm.data.model.Item
import com.ssafy.hm.ui.state.UploadViewModel
import com.ssafy.hm.ui.state.UploadViewModelFactory
import com.ssafy.hm.ui.theme.AuroraPurple
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminItemEditScreen(
    item: Item?,
    onBack: () -> Unit,
    onSave: (Item) -> Unit,
    uploadVm: UploadViewModel = viewModel(factory = UploadViewModelFactory())
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var name by remember { mutableStateOf(item?.itemName.orEmpty()) }
    var price by remember { mutableStateOf(item?.itemPrice?.toString().orEmpty()) }
    var count by remember { mutableStateOf(item?.itemCount?.toString().orEmpty()) }
    var comment by remember { mutableStateOf(item?.itemComment.orEmpty()) }
    var category by remember { mutableStateOf(item?.itemCategory ?: "시그니쳐") }
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
            title = { Text(if (item == null) "상품 추가" else "상품 수정", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("상품 이름") },
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
                value = price,
                onValueChange = { price = it },
                label = { Text("가격") },
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
                value = count,
                onValueChange = { count = it },
                label = { Text("수량") },
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
            Button(
                onClick = { showPicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AuroraPurple, contentColor = Color.White)
            ) {
                Text("이미지 등록")
            }
            val previewUrl = selectedUri ?: item?.itemPic ?: DEFAULT_ITEM_IMAGE_URL
            AsyncImage(
                model = previewUrl,
                contentDescription = "미리보기",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Text("카테고리")
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = category == "시그니쳐", onClick = { category = "시그니쳐" })
                    Text("시그니쳐")
                    Spacer(modifier = Modifier.width(12.dp))
                    RadioButton(selected = category == "악세서리", onClick = { category = "악세서리" })
                    Text("악세서리")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = category == "기념품", onClick = { category = "기념품" })
                    Text("기념품")
                    Spacer(modifier = Modifier.width(12.dp))
                    RadioButton(selected = category == "의류", onClick = { category = "의류" })
                    Text("의류")
                }
            }
            Button(
                onClick = {
                    if (saving) return@Button
                    saving = true
                    scope.launch {
                        try {
                            val finalPrice = price.toIntOrNull() ?: 0
                            val finalCount = count.toIntOrNull() ?: 0
                            val link = if (selectedUri != null) {
                                val part = createMultipart(context, selectedUri!!)
                                uploadVm.uploadAndGetLink(part)
                            } else {
                                item?.itemPic ?: DEFAULT_ITEM_IMAGE_URL
                            }
                            val payload = Item(
                                itemId = item?.itemId ?: 0,
                                itemName = name,
                                itemPrice = finalPrice,
                                itemCount = finalCount,
                                itemPic = link,
                                itemComment = comment.ifBlank { null },
                                itemCategory = category,
                                itemTime = item?.itemTime ?: nowString()
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
private const val DEFAULT_ITEM_IMAGE_URL = "https://example.com/default-item.jpg"

private fun nowString(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return LocalDateTime.now().format(formatter)
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
