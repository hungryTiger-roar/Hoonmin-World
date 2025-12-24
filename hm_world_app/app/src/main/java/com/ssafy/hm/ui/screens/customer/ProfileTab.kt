package com.ssafy.hm.ui.screens.customer

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.hm.R
import com.ssafy.hm.data.model.Account
import com.ssafy.hm.data.model.FriendWithDetails
import com.ssafy.hm.data.model.Item
import com.ssafy.hm.data.model.OrderDetail
import com.ssafy.hm.data.model.Orders
import com.ssafy.hm.ui.theme.AuroraGlow
import com.ssafy.hm.ui.theme.AuroraMist
import com.ssafy.hm.ui.theme.AuroraPurple
import com.ssafy.hm.ui.theme.AuroraPink
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

@Composable
fun ProfileTab(
    orders: List<Orders>,
    details: Map<Int, List<OrderDetail>>,
    items: Map<Int, Item>,
    onReceive: (Int) -> Unit,
    friends: List<FriendWithDetails>,
    availableFriends: List<FriendWithDetails>,
    accounts: List<Account>,
    onAddFriend: (String) -> Unit,
    onRemoveFriend: (Int) -> Unit,
    onToggleParty: (Int, String, Boolean) -> Unit,
    onLogout: () -> Unit,
    paddingValues: PaddingValues,
    account: Account?
) {
    val context = LocalContext.current
    var editing by remember { mutableStateOf(false) }
    var searching by remember { mutableStateOf(false) }
    var displayName by remember { mutableStateOf(account?.name.orEmpty()) }
    var displayId by remember { mutableStateOf(account?.userId.orEmpty()) }
    var displayPhone by remember { mutableStateOf(account?.phone.orEmpty()) }
    var displayBirth by remember { mutableStateOf(account?.birth.orEmpty()) }
    var removedFriendIds by remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(account) {
        if (!editing) {
            displayName = account?.name.orEmpty()
            displayId = account?.userId.orEmpty()
            displayPhone = account?.phone.orEmpty()
            displayBirth = account?.birth.orEmpty()
        }
    }
    LaunchedEffect(friends) {
        removedFriendIds = emptySet()
    }

    val totalSpent = remember(orders, details, items) {
        orders.sumOf { order ->
            details[order.orderId].orEmpty().sumOf { detail ->
                val price = items[detail.itemId]?.itemPrice ?: 0
                price * detail.orderQuantity
            }
        }
    }
    val moneyFormat = remember { NumberFormat.getNumberInstance(Locale.KOREA) }
    val spentText = remember(totalSpent) { moneyFormat.format(totalSpent) }
    val gradeStatus = remember(totalSpent) { resolveSpendingStatus(totalSpent) }
    val remainingText = remember(gradeStatus.remaining) { moneyFormat.format(gradeStatus.remaining) }

    if (editing) {
        EditProfileScreen(
            id = displayId,
            name = displayName,
            phone = displayPhone,
            birth = displayBirth,
            onCancel = { editing = false },
            onSave = { id, name, phone, birth, pw ->
                if (listOf(id, name, phone, birth, pw).any { it.isBlank() }) {
                    Toast.makeText(context, "빈칸이 있습니다. 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    displayName = name
                    displayPhone = phone
                    displayBirth = birth
                    editing = false
                }
            }
        )
        return
    }

    val sortedFriends = friends
        .filter { it.friend.id !in removedFriendIds }
        .sortedWith(
            compareByDescending<FriendWithDetails> { it.account.ticket }
                .thenByDescending { it.friend.friendParty }
                .thenBy { it.account.name ?: it.account.userId }
        )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AuroraPurple.copy(alpha = 0.15f),
                        AuroraGlow.copy(alpha = 0.12f),
                        AuroraPink.copy(alpha = 0.08f),
                        Color.White
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    AuroraPurple.copy(alpha = 0.9f),
                                    AuroraGlow.copy(alpha = 0.85f),
                                    AuroraPink.copy(alpha = 0.85f)
                                )
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "mypage",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("마이페이지", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.Logout, contentDescription = "logout", tint = Color.White)
                        }
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AuroraMist.copy(alpha = 0.8f))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                        AuroraGlow.copy(alpha = 0.7f),
                                        AuroraPurple.copy(alpha = 0.35f),
                                        Color.White.copy(alpha = 0.9f)
                                        )
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.hooni),
                                contentDescription = "profile",
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("이름: ${displayName.ifBlank { "-" }}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("아이디: ${displayId.ifBlank { "-" }}", fontSize = 13.sp)
                            Text("전화번호: ${displayPhone.ifBlank { "-" }}", fontSize = 13.sp)
                            Text("생년월일: ${displayBirth.ifBlank { "-" }}", fontSize = 13.sp)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AuroraMist.copy(alpha = 0.8f))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    "등급: ${gradeStatus.gradeName}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                                Text("누적 ${spentText}원", fontSize = 11.sp, color = Color.Gray)
                                LinearProgressIndicator(
                                    progress = { gradeStatus.progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(50)),
                                    color = AuroraPurple,
                                    trackColor = AuroraPurple.copy(alpha = 0.2f)
                                )
                                Text(
                                    text = if (gradeStatus.remaining == 0 && gradeStatus.isMaxGrade) {
                                        "최고 등급 달성"
                                    } else if (gradeStatus.nextGradeName == null) {
                                        "최고 등급까지 ${remainingText}원"
                                    } else {
                                        "다음 등급(${gradeStatus.nextGradeName})까지 ${remainingText}원"
                                    },
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                            OutlinedButton(
                                onClick = { editing = true },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("회원정보 수정", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "friends",
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("내 친구목록", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Text("체크박스를 통해 어트랙션 줄서기 그룹을 만들 수 있습니다! 😊", color = Color.Gray, fontSize = 10.sp)
            }

            items(sortedFriends, key = { it.friend.id }) { friend ->
                val hasTicket = friend.account.ticket
                val bg = if (hasTicket) Color.White else AuroraMist.copy(alpha = 0.5f)
                val alpha = if (hasTicket) 1f else 0.7f
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(alpha),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bg)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = friendAvatarRes(friend.friend.friendId)),
                                contentDescription = "friend",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("${friend.account.name ?: "이름없음"} (${friend.friend.friendId})")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (friend.account.ticket) {
                                Checkbox(
                                    checked = friend.friend.friendParty,
                                    onCheckedChange = { checked ->
                                    onToggleParty(friend.friend.id, friend.friend.friendId, checked)
                                    }
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    removedFriendIds = removedFriendIds + friend.friend.id
                                    onRemoveFriend(friend.friend.id)
                                }
                            ) {
                                Text("삭제")
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { searching = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = AuroraPurple
        ) {
            Icon(Icons.Default.Add, contentDescription = "add friend", tint = Color.White)
        }

        if (searching) {
            FriendSearchOverlay(
                friends = friends,
                accounts = accounts,
                currentUserId = (account?.userId ?: displayId).ifBlank { null },
                onClose = { searching = false },
                onAddFriend = onAddFriend
            )
        }
    }
}

@Composable
private fun FriendSearchOverlay(
    friends: List<FriendWithDetails>,
    accounts: List<Account>,
    currentUserId: String?,
    onClose: () -> Unit,
    onAddFriend: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val friendIds = remember(friends) { friends.map { it.friend.friendId }.toSet() }
    val filtered by remember(query, accounts, friendIds, currentUserId) {
        derivedStateOf {
            val q = query.trim()
            accounts.filter { candidate ->
                candidate.userId != currentUserId &&
                    !friendIds.contains(candidate.userId) &&
                    (q.isBlank() ||
                        candidate.name?.contains(q, ignoreCase = true) == true ||
                        candidate.userId.contains(q, ignoreCase = true))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(onClick = onClose)
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("친구 검색", fontWeight = FontWeight.Bold)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "close")
                    }
                }
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("이름 또는 아이디 검색") }
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (filtered.isEmpty()) {
                        Text("검색 결과가 없습니다.", color = Color.Gray)
                    } else {
                        filtered.forEach { candidate ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(AuroraMist, RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${candidate.name ?: "이름없음"} (${candidate.userId})")
                                Button(
                                    onClick = { onAddFriend(candidate.userId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AuroraGlow)
                                ) {
                                    Text("추가")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditProfileScreen(
    id: String,
    name: String,
    phone: String,
    birth: String,
    onCancel: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    val context = LocalContext.current
    var editId by remember { mutableStateOf(id) }
    var editName by remember { mutableStateOf(name) }
    var editPhone by remember { mutableStateOf(phone) }
    var editBirth by remember { mutableStateOf(birth) }
    var editPw by remember { mutableStateOf("") }
    val calendar = remember { Calendar.getInstance() }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            color = Color.White.copy(alpha = 0.24f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "back", tint = Color.White)
                    }
                    Text("회원정보 수정", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                OutlinedTextField(
                    value = editId,
                    onValueChange = { editId = it },
                    label = { RequiredLabel("아이디") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = editFieldColors()
                )
                OutlinedTextField(
                    value = editPw,
                    onValueChange = { editPw = it },
                    label = { RequiredLabel("비밀번호") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = editFieldColors()
                )
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { RequiredLabel("이름") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = editFieldColors()
                )
                OutlinedTextField(
                    value = editBirth,
                    onValueChange = { editBirth = it },
                    label = { RequiredLabel("생년월일") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = editFieldColors(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val year = calendar.get(Calendar.YEAR)
                                val month = calendar.get(Calendar.MONTH)
                                val day = calendar.get(Calendar.DAY_OF_MONTH)
                                DatePickerDialog(
                                    context,
                                    { _, y, m, d ->
                                        editBirth = "%04d-%02d-%02d".format(y, m + 1, d)
                                        calendar.set(y, m, d)
                                    },
                                    year,
                                    month,
                                    day
                                ).show()
                            }
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "calendar", tint = Color.White)
                        }
                    }
                )
                OutlinedTextField(
                    value = editPhone,
                    onValueChange = { editPhone = it },
                    label = { RequiredLabel("전화번호") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = editFieldColors()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        if (listOf(editId, editName, editPhone, editBirth, editPw).any { it.isBlank() }) {
                            Toast.makeText(context, "빈칸이 있습니다. 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                        } else {
                            onSave(editId, editName, editPhone, editBirth, editPw)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AuroraPurple),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("저장하기", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private data class SpendingTier(
    val name: String,
    val min: Int
)

private data class SpendingStatus(
    val gradeName: String,
    val nextGradeName: String?,
    val progress: Float,
    val remaining: Int,
    val isMaxGrade: Boolean
)

private fun resolveSpendingStatus(total: Int): SpendingStatus {
    val tiers = listOf(
        SpendingTier("\uD83C\uDF31 모험가", 0),
        SpendingTier("\uD83E\uDDED 탐험가", 50_000),
        SpendingTier("\uD83D\uDEE1\uFE0F 수호자", 150_000),
        SpendingTier("\uD83C\uDFC6 영웅", 300_000),
        SpendingTier("\uD83D\uDC51 마스터", 500_000)
    )
    val maxGoal = 1_000_000
    val currentIndex = tiers.indexOfLast { total >= it.min }.coerceAtLeast(0)
    val current = tiers[currentIndex]
    val next = tiers.getOrNull(currentIndex + 1)
    val nextGoal = next?.min ?: maxGoal
    val progress = if (total >= nextGoal) {
        1f
    } else {
        val span = (nextGoal - current.min).coerceAtLeast(1)
        ((total - current.min).toFloat() / span).coerceIn(0f, 1f)
    }
    val remaining = (nextGoal - total).coerceAtLeast(0)
    val isMaxGrade = next == null && total >= maxGoal
    return SpendingStatus(
        gradeName = current.name,
        nextGradeName = next?.name,
        progress = progress,
        remaining = remaining,
        isMaxGrade = isMaxGrade
    )
}

@Composable
private fun RequiredLabel(text: String) {
    Text(
        buildAnnotatedString {
            append(text)
            withStyle(SpanStyle(color = Color(0xFFFFB4C0))) { append(" *") }
        },
        color = Color.White
    )
}

@Composable
private fun editFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.White,
    unfocusedBorderColor = Color.Transparent,
    focusedLabelColor = AuroraGlow,
    unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
    cursorColor = Color.White,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = Color.White.copy(alpha = 0.18f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.14f),
    disabledTextColor = Color.White.copy(alpha = 0.9f),
    disabledLabelColor = Color.White.copy(alpha = 0.8f),
    disabledContainerColor = Color.White.copy(alpha = 0.14f)
)

private fun friendAvatarRes(friendId: String): Int {
    return if (abs(friendId.hashCode()) % 2 == 0) R.drawable.hooni else R.drawable.mini
}
