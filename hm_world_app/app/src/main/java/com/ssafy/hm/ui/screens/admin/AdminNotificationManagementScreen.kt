package com.ssafy.hm.ui.screens.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.hm.ui.theme.AuroraPurple
import java.util.Calendar
import java.util.Locale

private data class AdminPushItem(
    val id: Int,
    val title: String,
    val body: String,
    val schedule: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationManagementScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isRepeat by remember { mutableStateOf(false) }
    var time by remember { mutableStateOf("") }
    val selectedDays = remember { mutableStateListOf<String>() }

    var showLaterDialog by remember { mutableStateOf(false) }
    var laterDate by remember { mutableStateOf("") }
    var laterTime by remember { mutableStateOf("") }

    var editTarget by remember { mutableStateOf<AdminPushItem?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editContent by remember { mutableStateOf("") }
    var editSchedule by remember { mutableStateOf("") }

    val scheduledItems = remember { mutableStateListOf<AdminPushItem>() }
    val repeatItems = remember { mutableStateListOf<AdminPushItem>() }

    fun resetInput() {
        title = ""
        content = ""
        time = ""
        selectedDays.clear()
    }

    fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    if (showLaterDialog) {
        AlertDialog(
            onDismissRequest = { showLaterDialog = false },
            confirmButton = {
                Button(onClick = {
                    if (title.isBlank() || content.isBlank()) {
                        toast("제목과 내용을 입력해주세요.")
                        return@Button
                    }
                    if (laterDate.isBlank() || laterTime.isBlank()) {
                        toast("날짜와 시간을 입력해주세요.")
                        return@Button
                    }
                    val schedule = "${laterDate} ${laterTime}"
                    val id = (scheduledItems.maxOfOrNull { it.id } ?: 0) + 1
                    scheduledItems.add(AdminPushItem(id, title, content, schedule))
                    showLaterDialog = false
                    laterDate = ""
                    laterTime = ""
                    resetInput()
                    toast("PUSH 예약이 등록되었습니다.")
                }) { Text("저장") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLaterDialog = false }) { Text("취소") }
            },
            title = { Text("날짜/시간 선택") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showDatePicker(context, laterDate) { laterDate = it } },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (laterDate.isBlank()) "날짜 선택" else laterDate)
                    }
                    OutlinedButton(
                        onClick = { showTimePicker(context, laterTime) { laterTime = it } },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (laterTime.isBlank()) "시간 선택" else laterTime)
                    }
                }
            }
        )
    }

    if (editTarget != null) {
        AlertDialog(
            onDismissRequest = { editTarget = null },
            confirmButton = {
                Button(onClick = {
                    val target = editTarget ?: return@Button
                    val updated = AdminPushItem(target.id, editTitle, editContent, editSchedule)
                    val list = if (repeatItems.any { it.id == target.id }) repeatItems else scheduledItems
                    val index = list.indexOfFirst { it.id == target.id }
                    if (index != -1) list[index] = updated
                    editTarget = null
                    toast("PUSH 알림이 수정되었습니다")
                }) { Text("수정 완료") }
            },
            dismissButton = {
                OutlinedButton(onClick = { editTarget = null }) { Text("취소") }
            },
            title = { Text("알림 수정") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("제목") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editContent,
                        onValueChange = { editContent = it },
                        label = { Text("내용") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editSchedule,
                        onValueChange = { editSchedule = it },
                        label = { Text("시간/요일") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("알림 관리", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "back")
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
            Text("PUSH 알림 작성", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("제목") },
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedFieldColors()
            )
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("내용") },
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedFieldColors()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = !isRepeat, onClick = { isRepeat = false })
                Text("한 번만 보내기")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = isRepeat, onClick = { isRepeat = true })
                Text("반복하기")
            }

            if (!isRepeat) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            if (title.isBlank() || content.isBlank()) {
                                toast("제목과 내용을 입력해주세요.")
                            } else {
                                toast("PUSH 전송이 완료되었습니다!")
                                resetInput()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AuroraPurple, contentColor = Color.White),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("지금 보내기")
                    }
                    OutlinedButton(
                        onClick = {
                            if (title.isBlank() || content.isBlank()) {
                                toast("제목과 내용을 입력해주세요.")
                            } else {
                                showLaterDialog = true
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("나중에 보내기")
                    }
                }
            } else {
                Text("반복 요일 선택", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("월", "화", "수", "목", "금", "토", "일").forEach { day ->
                        DayChip(
                            label = day,
                            selected = selectedDays.contains(day),
                            onToggle = {
                                if (selectedDays.contains(day)) selectedDays.remove(day)
                                else selectedDays.add(day)
                            }
                        )
                    }
                }
                OutlinedButton(
                    onClick = { showTimePicker(context, time) { time = it } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (time.isBlank()) "시간 선택" else time)
                }
                Button(
                    onClick = {
                        if (title.isBlank() || content.isBlank() || time.isBlank() || selectedDays.isEmpty()) {
                            toast("요일과 시간을 입력해주세요.")
                        } else {
                            val schedule = "${selectedDays.joinToString(",")} ${time}"
                            val id = (repeatItems.maxOfOrNull { it.id } ?: 0) + 1
                            repeatItems.add(AdminPushItem(id, title, content, schedule))
                            toast("PUSH 반복 설정이 완료되었습니다!")
                            resetInput()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuroraPurple, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("반복 알림 저장")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("예약 알림", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (scheduledItems.isEmpty()) {
                Text("예약된 알림이 없습니다.", color = Color.Gray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(scheduledItems, key = { it.id }) { item ->
                        PushItemCard(
                            item = item,
                            onEdit = {
                                editTarget = item
                                editTitle = item.title
                                editContent = item.body
                                editSchedule = item.schedule
                            },
                            onDelete = { scheduledItems.remove(item) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("반복 알림", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (repeatItems.isEmpty()) {
                Text("반복 알림이 없습니다.", color = Color.Gray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(repeatItems, key = { it.id }) { item ->
                        PushItemCard(
                            item = item,
                            onEdit = {
                                editTarget = item
                                editTitle = item.title
                                editContent = item.body
                                editSchedule = item.schedule
                            },
                            onDelete = { repeatItems.remove(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayChip(label: String, selected: Boolean, onToggle: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable { onToggle() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = if (selected) Color.White else Color.DarkGray,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun PushItemCard(
    item: AdminPushItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(item.title, fontWeight = FontWeight.SemiBold)
                Text(item.body, fontSize = 12.sp, color = Color.DarkGray)
                Text(item.schedule, fontSize = 12.sp, color = Color.Gray)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "delete")
                }
            }
        }
    }
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.White,
    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)

private fun showTimePicker(context: Context, current: String, onPicked: (String) -> Unit) {
    val cal = Calendar.getInstance()
    val parts = current.split(":")
    val initHour = parts.getOrNull(0)?.toIntOrNull() ?: cal.get(Calendar.HOUR_OF_DAY)
    val initMinute = parts.getOrNull(1)?.toIntOrNull() ?: cal.get(Calendar.MINUTE)
    TimePickerDialog(
        context,
        { _, hour, minute ->
            val ampm = if (hour < 12) "오전" else "오후"
            val hour12 = ((hour + 11) % 12) + 1
            val formatted = String.format(Locale.getDefault(), "%s %02d:%02d", ampm, hour12, minute)
            onPicked(formatted)
        },
        initHour,
        initMinute,
        false
    ).show()
}

private fun showDatePicker(context: Context, current: String, onPicked: (String) -> Unit) {
    val cal = Calendar.getInstance()
    val parts = current.split("-")
    val initYear = parts.getOrNull(0)?.toIntOrNull() ?: cal.get(Calendar.YEAR)
    val initMonth = parts.getOrNull(1)?.toIntOrNull()?.minus(1) ?: cal.get(Calendar.MONTH)
    val initDay = parts.getOrNull(2)?.toIntOrNull() ?: cal.get(Calendar.DAY_OF_MONTH)
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onPicked(formatted)
        },
        initYear,
        initMonth,
        initDay
    ).show()
}
