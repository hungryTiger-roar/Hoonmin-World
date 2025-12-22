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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

    val filteredFriends = friendState.friends.filter { friendWithDetails ->
        !friendWithDetails.account.ticket
    }
    var expanded by remember { mutableStateOf(false) }
    var selectedFriends by remember { mutableStateOf<Set<FriendWithDetails>>(emptySet()) }

    LaunchedEffect(authState.account) {
        authState.account?.let {
            friendViewModel.setUser(it.userId)
        }
    }

    val userAge = authState.account?.birth?.let { getAge(it) }
    val userTicketType = userAge?.let { getTicketType(it) }

    val totalTickets = 1 + selectedFriends.size
    val adultCount = (if (userTicketType == "성인") 1 else 0) + selectedFriends.count {
        it.account.birth?.let { birth -> getTicketType(getAge(birth)) } == "성인"
    }
    val teenCount = (if (userTicketType == "청소년") 1 else 0) + selectedFriends.count {
        it.account.birth?.let { birth -> getTicketType(getAge(birth)) } == "청소년"
    }
    val childCount = (if (userTicketType == "어린이") 1 else 0) + selectedFriends.count {
        it.account.birth?.let { birth -> getTicketType(getAge(birth)) } == "어린이"
    }

    val totalPrice = (adultCount * 45000) + (teenCount * 35000) + (childCount * 30000)

    val summary = mutableListOf<String>()
    if (adultCount > 0) summary.add("성인 $adultCount"+"명")
    if (teenCount > 0) summary.add("청소년 $teenCount"+"명")
    if (childCount > 0) summary.add("어린이 $childCount"+"명")


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
                    Text(summary.joinToString(", "), fontSize = 16.sp)
                    Text("총 ${totalTickets}매", fontSize = 16.sp)
                    Text("${totalPrice}원", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Button(onClick = { /*TODO*/ }) {
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
                            Text(friendDetails.account.name ?: "이름 없음")
                        }
                    }
                }
            }
        }
    }
}


fun getAge(birthDate: String): Int? {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val birth = LocalDate.parse(birthDate, formatter)
        val today = LocalDate.now()
        var age = today.year - birth.year
        if (birth.month > today.month || (birth.month == today.month && birth.dayOfMonth > today.dayOfMonth)) {
            age--
        }
        age
    } catch (e: Exception) {
        null
    }
}

fun getTicketType(age: Int?): String {
    if (age == null) return "알수없음"
    return when {
        age >= 19 -> "성인"
        age in 13..18 -> "청소년"
        age in 4..12 -> "어린이"
        else -> "유아"
    }
}


@Preview(showBackground = true)
@Composable
fun TicketPurchaseScreenPreview() {
    HmWorldTheme {
        TicketPurchaseScreen()
    }
}