package com.ssafy.hm.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import android.widget.Toast
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.hm.data.model.FriendWithDetails
import com.ssafy.hm.ui.state.AuthViewModel
import com.ssafy.hm.ui.state.AuthViewModelFactory
import com.ssafy.hm.ui.state.FriendViewModel
import com.ssafy.hm.ui.state.FriendViewModelFactory
import com.ssafy.hm.ui.theme.HmWorldTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketPurchaseScreen(
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(LocalContext.current)),
    friendViewModel: FriendViewModel = viewModel(factory = FriendViewModelFactory()),
    onBack: () -> Unit = {}
) {
    val authState by authViewModel.uiState.collectAsState()
    val friendState by friendViewModel.state.collectAsState()
    val context = LocalContext.current

    val filteredFriends = friendState.friends.filter { friendWithDetails ->
        !friendWithDetails.account.ticket
    }
    var expanded by remember { mutableStateOf(false) }
    var selectedFriends by remember { mutableStateOf<Set<FriendWithDetails>>(emptySet()) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(authState.account) {
        authState.account?.let {
            friendViewModel.setUser(it.userId)
        }
    }

    val userAge = authState.account?.birth?.let { getAge(it) }
    val counts = mutableMapOf<TicketCategory, Int>().withDefault { 0 }
    fun countTicket(age: Int?) {
        val category = ticketCategory(age)
        counts[category] = counts.getValue(category) + 1
    }
    authState.account?.let { _ -> countTicket(userAge) }
    selectedFriends.forEach { friend ->
        val friendAge = friend.account.birth?.let { getAge(it) }
        countTicket(friendAge)
    }
    val totalTickets = counts.values.sum()
    val adultCount = counts[TicketCategory.ADULT] ?: 0
    val teenCount = counts[TicketCategory.TEEN] ?: 0
    val childCount = counts[TicketCategory.CHILD] ?: 0
    val babyCount = counts[TicketCategory.BABY] ?: 0
    val totalPrice = counts.entries.sumOf { (category, qty) -> category.price * qty }

    val ticketBreakdown = buildString {
        append("성인 ${adultCount}장")
        append(" · 청소년 ${teenCount}장")
        append(" · 어린이 ${childCount}장")
        if (babyCount > 0) {
            append(" · 유아 ${babyCount}장")
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("티켓 구매") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로가기",
                        modifier = Modifier.clickable { onBack() }
                    )
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(ticketBreakdown, fontSize = 16.sp)
                    Text(
                        text = "총 ${totalTickets}매 · ${formatCurrency(totalPrice)}원",
                        fontSize = 16.sp
                    )
                }
                Button(onClick = { showDialog = true }) {
                    Text("구매하기")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            PriceInfoCard()
            Spacer(modifier = Modifier.height(16.dp))
            NoticeCard()
            Spacer(modifier = Modifier.height(16.dp))
            FriendPurchaseCard(
                expanded = expanded,
                onExpandChange = { expanded = !expanded },
                friends = filteredFriends,
                selectedFriends = selectedFriends,
                onFriendSelected = { friend, isSelected ->
                    selectedFriends = if (isSelected) {
                        selectedFriends + friend
                    } else {
                        selectedFriends - friend
                    }
                }
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("티켓 구매") },
            text = {
                Text(
                    "성인 ${adultCount}장, 청소년 ${teenCount}장, 어린이 ${childCount}장 총 ${formatCurrency(totalPrice)}원 결제하시겠습니까?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        val targets = buildList {
                            authState.account?.let { add(it.copy(ticket = true)) }
                            selectedFriends.forEach { add(it.account.copy(ticket = true)) }
                        }
                        authViewModel.updateAccounts(targets) { success ->
                            if (success) {
                                friendViewModel.loadFriends()
                                authViewModel.refreshAccount()
                                selectedFriends = emptySet()
                                expanded = false
                                Toast.makeText(context, "티켓이 결제되었습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "티켓 결제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

@Composable
fun PriceInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("가격 안내", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            PriceRow("성인", "만 19세 이상", "45,000원")
            PriceRow("청소년", "만 13-18세", "35,000원")
            PriceRow("어린이", "만 4-12세", "30,000원")
        }
    }
}

@Composable
fun PriceRow(type: String, age: String, price: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Text(type, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(age, color = Color.Gray)
        }
        Text(price, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NoticeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("안내사항", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("∙ 구매한 티켓은 구매 당일에만 사용 가능합니다.")
        }
    }
}

@Composable
fun FriendPurchaseCard(
    expanded: Boolean,
    onExpandChange: () -> Unit,
    friends: List<FriendWithDetails>,
    selectedFriends: Set<FriendWithDetails>,
    onFriendSelected: (FriendWithDetails, Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandChange() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("친구 티켓 함께 구매하기", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "접기" else "펼치기"
                )
            }
            AnimatedVisibility(visible = expanded) {
                LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                    if (friends.isEmpty()){
                        item {
                            Text("친구 목록이 없습니다.")
                        }
                    }
                    items(friends) { friendDetails ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onFriendSelected(friendDetails, !selectedFriends.contains(friendDetails))
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedFriends.contains(friendDetails),
                                onCheckedChange = { isChecked ->
                                    onFriendSelected(friendDetails, isChecked)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val friendAge = friendDetails.account.birth?.let { getAge(it) }
                            val category = ticketCategory(friendAge)
                            Column {
                                Text(
                                    "${friendDetails.account.name ?: "이름 없음"} (${friendDetails.account.userId})",
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "${category.label} · ${formatCurrency(category.price)}원",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


fun getAge(birthDate: String?): Int? {
    if (birthDate.isNullOrBlank()) {
        return null
    }
    val parsers = listOf(
        DateTimeFormatter.ofPattern("yyyyMMdd"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyy.MM.dd"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd")
    )
    val birth = parsers.asSequence().mapNotNull { formatter ->
        runCatching { LocalDate.parse(birthDate, formatter) }.getOrNull()
    }.firstOrNull() ?: run {
        val digits = birthDate.filter { it.isDigit() }
        if (digits.length >= 8) {
            kotlin.runCatching {
                LocalDate.parse(digits.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"))
            }.getOrNull()
        } else {
            null
        }
    }
    return birth?.let {
        val today = LocalDate.now()
        var age = today.year - it.year
        if (it.month > today.month || (it.month == today.month && it.dayOfMonth > today.dayOfMonth)) {
            age--
        }
        age
    }
}

enum class TicketCategory(val label: String, val price: Int) {
    ADULT("성인", 45000),
    TEEN("청소년", 35000),
    CHILD("어린이", 30000),
    BABY("유아", 0)
}

fun ticketCategory(age: Int?): TicketCategory {
    return when {
        age == null -> TicketCategory.ADULT
        age >= 19 -> TicketCategory.ADULT
        age in 13..18 -> TicketCategory.TEEN
        age in 4..12 -> TicketCategory.CHILD
        age in 0..3 -> TicketCategory.BABY
        else -> TicketCategory.ADULT
    }
}

fun formatCurrency(amount: Int): String {
    return "%,d".format(amount)
}


@Preview(showBackground = true)
@Composable
fun TicketPurchaseScreenPreview() {
    HmWorldTheme {
        TicketPurchaseScreen()
    }
}
