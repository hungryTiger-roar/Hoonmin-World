package com.ssafy.hm.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.hm.R
import com.ssafy.hm.data.model.Item
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartScreen(
    cart: Map<Item, Int>,
    onChange: (Item, Int) -> Unit,
    onCheckout: () -> Unit,
    onBack: () -> Unit,
    onContinueShopping: () -> Unit
) {
    val background = Color(0xFFF7F1FA)
    val selection = remember(cart) {
        mutableStateMapOf<Int, Boolean>().apply {
            cart.keys.forEach { put(it.itemId, true) }
        }
    }
    val allSelected = cart.isNotEmpty() && selection.values.all { it }
    val totalAmount = cart.entries.sumOf { it.key.itemPrice * it.value }
    val totalText = NumberFormat.getNumberInstance(Locale.KOREA).format(totalAmount)

    Scaffold(
        containerColor = background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "장바구니",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF2A2430)
                )
                Spacer(modifier = Modifier.width(36.dp))
            }
        },
        bottomBar = {
            if (cart.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("총 결제 예상 금액", color = Color.Gray, fontSize = 12.sp)
                        Text(
                            text = "${totalText}원",
                            color = Color(0xFFE154B3),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Button(
                        onClick = onCheckout,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB259FF))
                    ) {
                        Text("구매하기", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        if (cart.isEmpty()) {
            EmptyCart(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                onContinueShopping = onContinueShopping
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = allSelected,
                        onCheckedChange = { checked ->
                            cart.keys.forEach { selection[it.itemId] = checked }
                        },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF6C4AE0))
                    )
                    Text(
                        text = "전체 선택",
                        fontSize = 13.sp,
                        color = Color(0xFF4A4451)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            cart.keys.filter { selection[it.itemId] == true }
                                .forEach { onChange(it, 0) }
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "선택삭제", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            }

            items(cart.entries.toList(), key = { it.key.itemId }) { (item, qty) ->
                CartItemCard(
                    item = item,
                    qty = qty,
                    checked = selection[item.itemId] == true,
                    onCheckedChange = { selection[item.itemId] = it },
                    onDecrease = { onChange(item, qty - 1) },
                    onIncrease = { onChange(item, qty + 1) }
                )
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: Item,
    qty: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    val priceText = NumberFormat.getNumberInstance(Locale.KOREA).format(item.itemPrice)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF6C4AE0))
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = item.itemPic?.takeIf { it.isNotBlank() },
                    contentDescription = item.itemName,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.noimage),
                    error = painterResource(id = R.drawable.noimage),
                    fallback = painterResource(id = R.drawable.noimage)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.itemName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF2A2430),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.itemComment ?: "",
                        fontSize = 12.sp,
                        color = Color(0xFF7A7282),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = Color(0xFFB9A9D6)
                            )
                        }
                        Text(text = qty.toString(), fontWeight = FontWeight.Bold)
                        IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = Color(0xFFB259FF)
                            )
                        }
                    }
                }
                Text(
                    text = "${priceText}원",
                    color = Color(0xFFB259FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyCart(
    modifier: Modifier,
    onContinueShopping: () -> Unit
) {
    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF7F1FA), Color(0xFFF5F0FF))
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFFEAD8FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = Color(0xFFB259FF),
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "장바구니가 비었어요", color = Color(0xFF6B6572), fontSize = 14.sp)
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = onContinueShopping,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB259FF)),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
        ) {
            Text("쇼핑 계속하기", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
