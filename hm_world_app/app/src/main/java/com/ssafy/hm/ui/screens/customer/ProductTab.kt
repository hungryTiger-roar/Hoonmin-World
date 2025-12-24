package com.ssafy.hm.ui.screens.customer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.ssafy.hm.R
import com.ssafy.hm.data.model.AiSearchResult
import com.ssafy.hm.data.model.BuyImage
import com.ssafy.hm.data.model.Item
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import android.content.ContentResolver
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductTab(
    list: List<Item>,
    buyImages: List<BuyImage>,
    aiResultIds: List<Int>,
    aiResults: List<AiSearchResult>,
    aiLoading: Boolean,
    onAiSearch: (MultipartBody.Part) -> Unit,
    onClearAiSearch: () -> Unit,
    onSelect: (Int) -> Unit,
    onAddCart: (Item) -> Unit,
    onOpenCart: () -> Unit,
    cartCount: Int,
    onOpenOrderHistory: () -> Unit,
    paddingValues: PaddingValues
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("전체") }
    val actionColor = Color.Black

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var hasImage by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var lastSearchedUri by remember { mutableStateOf<Uri?>(null) }
    var lastDialogUri by remember { mutableStateOf<Uri?>(null) }
    var showAiDialog by remember { mutableStateOf(false) }
    var dialogItems by remember { mutableStateOf<List<Item>>(emptyList()) }
    var dialogIsFallback by remember { mutableStateOf(false) }
    val aiScoreMap = remember(aiResults) { aiResults.associate { it.itemId to it.score } }
    val itemMap = remember(list) { list.associateBy { it.itemId } }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                hasImage = true
                imageUri = uri
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                val newImageUri = createImageUri(context)
                imageUri = newImageUri
                cameraLauncher.launch(newImageUri)
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(hasImage, imageUri) {
        val uri = imageUri
        if (hasImage && uri != null && uri != lastSearchedUri) {
            runCatching {
                val part = createMultipart(context, uri)
                onClearAiSearch()
                onAiSearch(part)
                lastSearchedUri = uri
            }.onFailure {
                Toast.makeText(context, "AI search failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = Color(0xFF6A5AE0),
                shape = CircleShape
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "AI Image Search", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9F9F9))
        ) {
            ProductTopBar(
                onCartClick = onOpenCart,
                onPurchaseHistoryClick = onOpenOrderHistory,
                cartCount = cartCount,
                actionColor = actionColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                ProductSearchBar(query = query, onQueryChange = { query = it })
                Spacer(modifier = Modifier.height(16.dp))
                if (aiLoading) {
                    Text("AI searching...", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (aiResultIds.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F0FF))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF6A5AE0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("AI recommendations", fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "${aiResultIds.size} items found",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            TextButton(onClick = onClearAiSearch) { Text("Show all") }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            val filteredList = list.filter {
                (selectedCategory == "전체" || it.itemCategory == selectedCategory) &&
                        (query.isBlank() || it.itemName.contains(query, ignoreCase = true))
            }

            val aiResultSet = remember(aiResultIds) { aiResultIds.toSet() }
            val displayList = if (aiResultIds.isNotEmpty()) {
                list.filter { aiResultSet.contains(it.itemId) }
            } else {
                filteredList
            }

            LaunchedEffect(aiLoading, aiResults, lastSearchedUri, filteredList) {
                val uri = lastSearchedUri
                if (!aiLoading && uri != null && uri != lastDialogUri) {
                    val ordered = aiResults.mapNotNull { itemMap[it.itemId] }
                    val candidates = if (ordered.isNotEmpty()) {
                        dialogIsFallback = false
                        ordered
                    } else {
                        dialogIsFallback = true
                        filteredList
                    }
                    dialogItems = candidates.take(3)
                    showAiDialog = dialogItems.isNotEmpty()
                    lastDialogUri = uri
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 30.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    ProductCarousel(images = buyImages)
                }

                item(span = { GridItemSpan(2) }) {
                    Column {
                        CategoryButtons(
                            selectedCategory = selectedCategory,
                            onCategorySelected = { selectedCategory = it }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (aiResultIds.isNotEmpty()) "AI 추천 상품" else "추천 상품",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                items(displayList, key = { it.itemId }) { item ->
                    val aiScore = aiScoreMap[item.itemId]
                    ProductCard(
                        item = item,
                        onAddToCartClick = { onAddCart(item) },
                        onCardClick = { onSelect(item.itemId) },
                        aiScore = aiScore
                    )
                }
            }
        }

        if (showAiDialog) {
            AlertDialog(
                onDismissRequest = { showAiDialog = false },
                title = { Text("AI 추천 결과") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (dialogIsFallback) {
                            Text(
                                text = "사진과 유사한 상품을 추천해드려요.",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                        dialogItems.forEach { item ->
                            val score = aiScoreMap[item.itemId]
                            val matchPercent = score?.let { (it * 100).coerceIn(0.0, 100.0).toInt() }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showAiDialog = false
                                        onSelect(item.itemId)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = item.itemPic?.takeIf { it.isNotBlank() },
                                    contentDescription = item.itemName,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(id = R.drawable.noimage),
                                    error = painterResource(id = R.drawable.noimage),
                                    fallback = painterResource(id = R.drawable.noimage)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.itemName, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                    Text("${item.itemPrice}원", color = Color.Gray, fontSize = 12.sp)
                                }
                                if (matchPercent != null) {
                                    Text(
                                        text = "Match ${matchPercent}%",
                                        color = Color(0xFF6A5AE0),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAiDialog = false }) { Text("닫기") }
                }
            )
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                AiImageSearchSheet(
                    onOpenCamera = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                                val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                    val newImageUri = createImageUri(context)
                                    imageUri = newImageUri
                                    cameraLauncher.launch(newImageUri)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                        }
                    },
                    onOpenGallery = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                        }
                    }
                )
            }
        }
    }
}

private fun createImageUri(context: Context): Uri {
    val imagePath = File(context.cacheDir, "images")
    if (!imagePath.exists()) imagePath.mkdirs()
    val imageFile = File.createTempFile(
        "camera_${System.currentTimeMillis()}",
        ".jpg",
        imagePath
    )
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        authority,
        imageFile
    )
}

private fun createMultipart(context: Context, uri: Uri): MultipartBody.Part {
    val resolver = context.contentResolver
    val fileName = getFileName(resolver, uri)
    val mimeType = resolver.getType(uri) ?: "image/jpeg"
    val file = copyUriToFile(context, uri, fileName)
    if (file.length() == 0L) {
        throw IllegalStateException("Empty image file")
    }
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
        ?.let { ".${it}" }
        ?: ".jpg"
    return "upload_${System.currentTimeMillis()}$extension"
}

private fun copyUriToFile(context: Context, uri: Uri, fileName: String): File {
    val dir = File(context.cacheDir, "uploads")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    val file = File(dir, fileName)
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalStateException("Unable to open image input stream")
    inputStream.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    return file
}


@Composable
fun AiImageSearchSheet(onOpenCamera: () -> Unit, onOpenGallery: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("AI 이미지 검색", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("사진을 업로드하면 비슷한 상품을 찾아드려요", color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenCamera),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC).copy(alpha = 0.5f))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    tint = Color(0xFFE91E63),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("카메라 열기", fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Text("지금 바로 사진 찍기", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenGallery),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD).copy(alpha = 0.5f))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.PhotoLibrary,
                    contentDescription = "Gallery",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("갤러리에서 선택", fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Text("저장된 사진 선택하기", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}
@Composable
fun ProductTopBar(
    onCartClick: () -> Unit,
    onPurchaseHistoryClick: () -> Unit,
    cartCount: Int,
    actionColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Store,
            contentDescription = "Logo",
            tint = Color(0xFF6200EE),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("훈민월드", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onPurchaseHistoryClick) {
            Icon(
                imageVector = Icons.Default.ReceiptLong,
                contentDescription = "Purchase History",
                tint = actionColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("구매내역", color = actionColor)
        }
        CartIconWithBadge(cartCount = cartCount, onClick = onCartClick, tint = actionColor)
    }
}

@Composable
private fun CartIconWithBadge(cartCount: Int, onClick: () -> Unit, tint: Color) {
    Box {
        IconButton(onClick = onClick) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = tint)
        }
        if (cartCount > 0) {
            val label = if (cartCount > 99) "99+" else cartCount.toString()
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-2).dp, y = 2.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF4D6D)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProductSearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("상품 검색") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun ProductCarousel(images: List<BuyImage>) {
    val pagerState = rememberPagerState(pageCount = { images.size.coerceAtLeast(1) })

    LaunchedEffect(pagerState.pageCount) {
        while (pagerState.pageCount > 1) {
            delay(3000)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % pagerState.pageCount)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            HorizontalPager(state = pagerState) { page ->
                val image = images.getOrNull(page)
                Box(contentAlignment = Alignment.BottomStart) {
                    if (image != null) {
                        AsyncImage(
                            model = image.buyImage,
                            contentDescription = "Buy Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            alignment = Alignment.TopCenter
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("이미지를 준비중입니다.", color = Color.Gray)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(pagerState.pageCount) { index ->
                val color = if (pagerState.currentPage == index) Color(0xFF6200EE) else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryButtons(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf("전체", "신상품", "문구/잡화", "액세서리", "의류")

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Button(
                onClick = { onCategorySelected(category) },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF6200EE) else Color.White,
                    contentColor = if (isSelected) Color.White else Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(category)
            }
        }
    }
}

@Composable
fun ProductCard(
    item: Item,
    onAddToCartClick: () -> Unit,
    onCardClick: () -> Unit,
    aiScore: Double? = null
) {
    val isAiMatch = aiScore != null
    val matchPercent = aiScore?.let { (it * 100).coerceIn(0.0, 100.0).toInt() }
    Card(
        modifier = Modifier
            .clickable(onClick = onCardClick)
            .border(
                width = if (isAiMatch) 1.5.dp else 0.dp,
                color = if (isAiMatch) Color(0xFF6A5AE0) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = item.itemPic?.takeIf { it.isNotBlank() },
                    contentDescription = item.itemName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.noimage),
                    error = painterResource(id = R.drawable.noimage),
                    fallback = painterResource(id = R.drawable.noimage)
                )
                if (isAiMatch && matchPercent != null) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color(0xFF6A5AE0))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI match",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Match ${matchPercent}%",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                if (item.itemCategory == "신상품") {
                    Surface(
                        color = Color(0xFFE91E63).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "New",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "NEW",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(item.itemName, fontWeight = FontWeight.Bold, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${item.itemPrice}원", color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onAddToCartClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF8A2BE2), Color(0xFFFF69B4))
                                )
                            )
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("담기", color = Color.White)
                    }
                }
            }
        }
    }
}
