package com.ssafy.hm.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.hm.data.model.Account
import com.ssafy.hm.data.model.Attraction
import com.ssafy.hm.data.model.AttractionLine
import com.ssafy.hm.ui.state.NfcTagBus
import com.ssafy.hm.ui.theme.AuroraPurple
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAttractionLineManagementScreen(
    attraction: Attraction,
    lines: List<AttractionLine>,
    accounts: Map<String, Account>,
    loading: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onNoShowLine: (Int) -> Unit,
    onNoShowMember: (Int, String) -> Unit
) {
    val context = LocalContext.current
    val boardingLineIds = remember { mutableStateListOf<Int>() }
    val readyMembers = remember { mutableStateListOf<String>() }
    var expandedLineId by remember { mutableStateOf<Int?>(null) }
    var missingDialogName by remember { mutableStateOf<String?>(null) }
    val latestLines by rememberUpdatedState(lines)
    val latestAccounts by rememberUpdatedState(accounts)

    val capacity = attraction.attCapacity
    val waitCount = lines.sumOf { it.members.size }
    val groupCount = lines.size

    fun fillBoarding() {
        if (capacity <= 0) {
            Toast.makeText(context, "최대 탑승 인원을 확인해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val sorted = lines.sortedBy { it.lineId }
        val selected = mutableListOf<Int>()
        var total = 0
        for (line in sorted) {
            val size = line.members.size
            if (size == 0) continue
            if (total + size <= capacity || selected.isEmpty()) {
                selected.add(line.lineId)
                total += size
            }
            if (total >= capacity) break
        }
        if (selected.isEmpty()) {
            Toast.makeText(context, "추가 가능한 그룹이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        boardingLineIds.clear()
        boardingLineIds.addAll(selected)
        readyMembers.clear()
    }

    fun completeBoarding() {
        val boardingLines = lines.filter { boardingLineIds.contains(it.lineId) }
        val members = boardingLines.flatMap { it.members }
        if (members.isEmpty()) {
            Toast.makeText(context, "탑승 목록이 비어있습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if (readyMembers.size < members.size) {
            Toast.makeText(context, "아직 준비되지 않은 인원이 있습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        members.forEach { member ->
            onNoShowMember(member.lineId, member.userId)
        }
        boardingLineIds.clear()
        readyMembers.clear()
        onRefresh()
    }

    fun handleNfcTag(userId: String) {
        val boardingMembers = latestLines
            .filter { boardingLineIds.contains(it.lineId) }
            .flatMap { it.members }
        val target = boardingMembers.firstOrNull { it.userId == userId }
        if (target != null) {
            if (!readyMembers.contains(userId)) {
                readyMembers.add(userId)
                Toast.makeText(context, "NFC 확인 완료", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "이미 준비 완료된 인원입니다.", Toast.LENGTH_SHORT).show()
            }
            missingDialogName = null
        } else {
            val name = latestAccounts[userId]?.name ?: userId
            missingDialogName = name
            Toast.makeText(context, "탑승 명단에 없는 태그입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        NfcTagBus.tags.collectLatest { tagValue ->
            handleNfcTag(tagValue)
        }
    }

    if (missingDialogName != null) {
        AlertDialog(
            onDismissRequest = { missingDialogName = null },
            confirmButton = {
                Button(onClick = { missingDialogName = null }) { Text("확인") }
            },
            title = { Text("탑승 명단 없음") },
            text = { Text("${missingDialogName}님이 현재 탑승 명단에 없습니다.") }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("어트랙션 관리", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "back")
                }
            },
            actions = {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "refresh")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(attraction.attName ?: "어트랙션", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("최대 탑승 인원: ${capacity}명", fontSize = 12.sp)
                    Text("현재 대기 인원: ${waitCount}명", fontSize = 12.sp)
                    Text("예약 그룹 수: ${groupCount}개", fontSize = 12.sp)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "탑승 인원 ${boardingLineIds.sumOf { id -> lines.firstOrNull { it.lineId == id }?.members?.size ?: 0 }} / ${capacity}명"
                    )
                    Row {
                        Button(
                            onClick = { fillBoarding() },
                            colors = ButtonDefaults.buttonColors(containerColor = AuroraPurple, contentColor = Color.White),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("인원 채워 넣기", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { completeBoarding() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("입장 완료", fontSize = 12.sp)
                        }
                    }
                }
            }

            if (loading) {
                Text("불러오는 중...", color = Color.Gray)
            } else if (boardingLineIds.isEmpty()) {
                Text("현재 탑승 목록이 없습니다.", color = Color.Gray)
            } else {
                val boardingLines = lines.filter { boardingLineIds.contains(it.lineId) }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(boardingLines, key = { it.lineId }) { line ->
                        val first = line.members.firstOrNull()
                        val account = first?.userId?.let { accounts[it] }
                        val name = account?.name ?: first?.userId.orEmpty()
                        val phoneSuffix = account?.phone?.takeLast(4) ?: "--"
                        val size = line.members.size
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            expandedLineId = if (expandedLineId == line.lineId) null else line.lineId
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("$name · ${size}명", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text("연락처 뒷번호: $phoneSuffix", fontSize = 12.sp, color = Color.Gray)
                                    }
                                    Button(
                                        onClick = { onNoShowLine(line.lineId) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD65D5D), contentColor = Color.White),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("노쇼", fontSize = 12.sp)
                                    }
                                }
                                if (expandedLineId == line.lineId) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        line.members.forEach { member ->
                                            val memberAccount = accounts[member.userId]
                                            val memberName = memberAccount?.name ?: member.userId
                                            val ready = readyMembers.contains(member.userId)
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(memberName, fontSize = 12.sp)
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Button(
                                                        onClick = {
                                                            if (ready) {
                                                                readyMembers.remove(member.userId)
                                                            } else {
                                                                readyMembers.add(member.userId)
                                                            }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = if (ready) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                                                            contentColor = Color.White
                                                        ),
                                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(if (ready) "준비 완료" else "NFC 태그", fontSize = 11.sp)
                                                    }
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Button(
                                                        onClick = { onNoShowMember(member.lineId, member.userId) },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD65D5D), contentColor = Color.White),
                                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                                    ) {
                                                        Text("노쇼", fontSize = 11.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
