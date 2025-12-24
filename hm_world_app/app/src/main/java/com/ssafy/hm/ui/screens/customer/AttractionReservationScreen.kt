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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.hm.data.model.Attraction
import com.ssafy.hm.data.model.FriendWithDetails
import com.ssafy.hm.ui.state.AuthViewModel
import com.ssafy.hm.ui.state.FriendViewModel
import com.ssafy.hm.ui.state.LineViewModel
import com.ssafy.hm.ui.theme.AuroraGlow
import com.ssafy.hm.ui.theme.AuroraPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionReservationScreen(
    attraction: Attraction?,
    attractions: List<Attraction>,
    onBack: () -> Unit,
    onReservationComplete: () -> Unit,
    friendViewModel: FriendViewModel,
    authViewModel: AuthViewModel,
    lineViewModel: LineViewModel,
) {
    val friendState by friendViewModel.state.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val lineState by lineViewModel.state.collectAsState()
    val currentUser = authState.account

    var selectedFriends by remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(Unit) {
        friendViewModel.loadFriends()
    }

    LaunchedEffect(attractions) {
        val attIds = attractions.map { it.attId }
        if (attIds.isNotEmpty()) {
            lineViewModel.loadWaitingUsers(attIds)
        } else {
            lineViewModel.clearWaitingUsers()
        }
    }

    val waitingUserIds = lineState.waitingUserIds

    LaunchedEffect(friendState.friends, currentUser, waitingUserIds) {
        val currentUserId = currentUser?.userId ?: ""
        val initialSelected = friendState.friends
            .filter { (it.account.ticket ?: false) && it.friend.friendParty }
            .map { it.friend.friendId }
            .filterNot { waitingUserIds.contains(it) }
            .toMutableSet()
        if (currentUser?.ticket == true && !waitingUserIds.contains(currentUserId)) {
            initialSelected.add(currentUserId)
        }
        selectedFriends = initialSelected
    }

    val friendsWithTickets = remember(friendState.friends, waitingUserIds) {
        friendState.friends
            .filter { it.account.ticket ?: false }
            .sortedBy { waitingUserIds.contains(it.friend.friendId) }
    }

    val selectedCount = selectedFriends.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("어트랙션 예약", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (attraction != null && selectedFriends.isNotEmpty()) {
                        lineViewModel.createLine(attraction.attId, selectedFriends.toList()) { success ->
                            if (success) {
                                authViewModel.refreshAccount()
                                onReservationComplete()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                enabled = selectedCount > 0 && attraction != null
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = if (selectedCount > 0 && attraction != null) {
                                    listOf(Color(0xFF9C27FF), Color(0xFFFF5AA4))
                                } else {
                                    listOf(Color.Gray, Color.LightGray)
                                }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedCount > 0) "${selectedCount}명 예약하기" else "예약하기",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF7F3FA))
        ) {
            if (attraction != null) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = attraction.attName ?: "놀이기구",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                            Text(
                                text = "탑승 가능 인원 ${attraction.attCapacity}명 / 회차",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AuroraGlow.copy(alpha = 0.3f),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("예약 인원", fontSize = 12.sp, color = AuroraPurple)
                                Text(
                                    "${selectedCount}명",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AuroraPurple
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE7DEEE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "오늘 티켓을 보유한 친구만 예약할 수 있습니다.",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(friendsWithTickets, key = { it.friend.id }) { friendDetails ->
                            val friendId = friendDetails.friend.friendId
                            val isWaiting = waitingUserIds.contains(friendId)
                            FriendSelectItem(
                                friendDetails = friendDetails,
                                isSelected = friendId in selectedFriends,
                                isWaiting = isWaiting,
                                onToggle = { isChecked ->
                                    if (isWaiting) return@FriendSelectItem
                                    val newSelected = selectedFriends.toMutableSet()
                                    if (isChecked) {
                                        newSelected.add(friendId)
                                    } else {
                                        newSelected.remove(friendId)
                                    }
                                    selectedFriends = newSelected
                                    friendViewModel.updateFriendParty(friendDetails.friend.id, friendId, isChecked)
                                }
                            )
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("놀이기구 정보를 불러오지 못했습니다.")
                }
            }
        }
    }
}

@Composable
fun FriendSelectItem(
    friendDetails: FriendWithDetails,
    isSelected: Boolean,
    isWaiting: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val cardColor = if (isWaiting) Color(0xFFEAEAEA) else Color.White
    val textColor = if (isWaiting) Color.Gray else Color.Black
    val subTextColor = if (isWaiting) Color.Gray else Color.Gray
    val avatarColor = if (isWaiting) Color.LightGray else AuroraGlow.copy(alpha = if (isSelected) 1f else 0.3f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = friendDetails.account.name?.firstOrNull()?.toString() ?: "",
                    color = if (isWaiting) Color.White else if (isSelected) Color.White else AuroraPurple,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friendDetails.account.name ?: "",
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = "@${friendDetails.account.userId}",
                    color = subTextColor,
                    fontSize = 12.sp
                )
                if (isWaiting) {
                    Text(
                        text = "이미 대기중 입니다",
                        color = Color(0xFFB85C5C),
                        fontSize = 12.sp
                    )
                }
            }
            IconButton(onClick = { onToggle(!isSelected) }, enabled = !isWaiting) {
                Icon(
                    imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                    contentDescription = "선택",
                    tint = if (isSelected) AuroraPurple else Color.LightGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
