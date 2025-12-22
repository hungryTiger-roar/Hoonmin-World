package com.ssafy.hm.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.hm.R
import com.ssafy.hm.data.model.BuyImage
import com.ssafy.hm.data.model.Item
import com.ssafy.hm.data.model.ItemReview // Moved this import to the top
import kotlinx.coroutines.delay

@Composable
fun ProductTab(
    list: List<Item>,
    buyImages: List<BuyImage>,
    onSelect: (Int) -> Unit,
    onAddCart: (Item) -> Unit,
    paddingValues: PaddingValues
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("전체") }

    // 1. 기존 Column을 Box로 변경하여 고정 UI와 스크롤 UI를 분리합니다.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues) // Scaffold로부터 받은 패딩 적용
            .background(Color(0xFFF9F9F9))
    ) {
        // --- 스크롤되지 않는 고정 영역 ---
        ProductTopBar()
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            ProductSearchBar(query = query, onQueryChange = { query = it })
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- 여기부터 스크롤되는 영역 ---
        val filteredList = list.filter {
            (selectedCategory == "전체" || it.itemCategory == selectedCategory) &&
                    (query.isBlank() || it.itemName.contains(query, ignoreCase = true))
        }

        // 2. LazyVerticalGrid를 사용하여 스크롤 영역을 만듭니다.
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            // 그리드의 상단과 하단에 패딩을 추가합니다.
            contentPadding = PaddingValues(top = 8.dp, bottom = 30.dp)
        ) {
            // 3. 고정 UI였던 컴포저블들을 LazyGrid의 'item'으로 추가합니다.
            //    span을 사용하여 이 아이템들이 한 줄을 모두 차지하도록 설정합니다.
            item(span = { GridItemSpan(2) }) {
                ProductCarousel(images = buyImages)
            }

            item(span = { GridItemSpan(2) }) {
                Column { // 여백을 주기 위해 Column으로 감쌉니다.
                    Spacer(modifier = Modifier.height(24.dp))
                    CategoryButtons(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "추천 상품",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            // 4. 기존 상품 목록을 'items'로 추가합니다.
            items(filteredList, key = { it.itemId }) { item ->
                ProductCard(
                    item = item,
                    onAddToCartClick = { onAddCart(item) },
                    onCardClick = { onSelect(item.itemId) }
                )
            }
        }
    }
}


@Composable
fun ProductTopBar() {
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
        TextButton(onClick = { /* TODO: 구매내역 화면 이동 */ }) {
            Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = "Purchase History", tint = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            Text("구매내역", color = Color.Gray)
        }
        IconButton(onClick = { /* TODO: 장바구니 화면 이동 */ }) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.Gray)
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
                .height(150.dp),
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
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("이미지를 준비 중입니다.", color = Color.Gray)
                        }
                    }
                     Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.5f)
                                    ),
                                    startY = 250f
                                )
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                             Text(
                                "겨울 시즌 특별 할인",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                             Text(
                                "전 상품 최대 20% 할인",
                                color = Color.White,
                                fontSize = 14.sp
                            )
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
    // TODO: 여기에 카테고리 종류를 추가하세요
    val categories = listOf("전체", "신상품", "머리띠", "가방", "의류", "인형")

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
fun ProductGrid(
    items: List<Item>,
    onItemClick: (Int) -> Unit,
    onAddToCartClick: (Item) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items, key = { it.itemId }) { item ->
            ProductCard(item = item, onAddToCartClick = { onAddToCartClick(item) }, onCardClick = { onItemClick(item.itemId) })
        }
    }
}

@Composable
fun ProductCard(item: Item, onAddToCartClick: () -> Unit, onCardClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onCardClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(contentAlignment = Alignment.TopEnd) {
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
                if (item.itemCategory == "신상품") { // Example condition for "NEW" badge
                     Surface(
                        color = Color(0xFFE91E63).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        modifier = Modifier.padding(top = 8.dp, end = 8.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                           Icon(Icons.Default.Star, contentDescription = "New", tint=Color.White, modifier = Modifier.size(14.dp))
                           Spacer(modifier = Modifier.width(2.dp))
                           Text(
                                text = "NEW",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(item.itemName ?: "", fontWeight = FontWeight.Bold, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${item.itemPrice}원", color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onAddToCartClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp) // Remove default padding
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF8A2BE2), Color(0xFFFF69B4))
                                )
                            )
                            .padding(vertical = 12.dp), // Apply padding to the Box
                        contentAlignment = Alignment.Center
                    ) {
                        Text("담기", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ItemDetailDialog(
    item: Item,
    reviews: List<ItemReview>,
    onAddCart: (Item) -> Unit, // Modified to accept Item
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row {
                TextButton(onClick = { onAddCart(item) }) { Text("장바구니") } // Pass item to onAddCart
                TextButton(onClick = onDismiss) { Text("닫기") }
            }
        },
        title = { Text(item.itemName ?: "") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("가격 ${item.itemPrice}원")
                Text(item.itemComment ?: "")
                Text("리뷰 (${reviews.size})", fontWeight = FontWeight.Bold)
                reviews.forEach { r -> Text("- ${r.itemReviewComment ?: ""} (${r.itemRating}점)") }
            }
        }
    )
}

