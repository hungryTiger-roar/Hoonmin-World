package com.ssafy.hm.ui.screens.customer

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.hm.R
import com.ssafy.hm.data.model.BuyImage
import com.ssafy.hm.data.model.Item
import kotlinx.coroutines.delay

@Composable
fun ProductTab(
    list: List<Item>,
    buyImages: List<BuyImage>,
    onSelect: (Int) -> Unit,
    onAddCart: (Item) -> Unit,
    onOpenCart: () -> Unit,
    onOpenOrderHistory: () -> Unit,
    paddingValues: PaddingValues
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("전체") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFFF9F9F9))
    ) {
        ProductTopBar(onCartClick = onOpenCart, onPurchaseHistoryClick = onOpenOrderHistory)
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            ProductSearchBar(query = query, onQueryChange = { query = it })
            Spacer(modifier = Modifier.height(16.dp))
        }

        val filteredList = list.filter {
            (selectedCategory == "전체" || it.itemCategory == selectedCategory) &&
                (query.isBlank() || it.itemName.contains(query, ignoreCase = true))
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
                        text = "추천 상품",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

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
fun ProductTopBar(onCartClick: () -> Unit, onPurchaseHistoryClick: () -> Unit) {
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
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("구매내역", color = Color.Gray)
        }
        IconButton(onClick = onCartClick) {
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
                if (item.itemCategory == "신상품") {
                    Surface(
                        color = Color(0xFFE91E63).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        modifier = Modifier.padding(top = 8.dp, end = 8.dp)
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


