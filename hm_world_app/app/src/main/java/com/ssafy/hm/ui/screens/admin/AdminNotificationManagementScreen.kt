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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.hm.data.model.NotificationRepeatRequest
import com.ssafy.hm.data.model.NotificationScheduleRequest
import com.ssafy.hm.data.model.NotificationSendRequest
import com.ssafy.hm.data.model.NotificationUpdateRequest
import com.ssafy.hm.data.model.PushNotification
import com.ssafy.hm.data.network.NetworkModule
import com.ssafy.hm.ui.theme.AuroraPurple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

private data class DayOption(val label: String, val code: String)

private val dayOptions = listOf(
    DayOption("월", "MON"),
    DayOption("화", "TUE"),
    DayOption("수", "WED"),
    DayOption("목", "THU"),
    DayOption("금", "FRI"),
    DayOption("토", "SAT"),
    DayOption("일", "SUN")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationManagementScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isRepeat by remember { mutableStateOf(false) }
    var time by remember { mutableStateOf("") }
    val selectedDays = remember { mutableStateListOf<String>() }

    var showLaterDialog by remember { mutableStateOf(false) }
    var laterDate by remember { mutableStateOf("") }
    var laterTime by remember { mutableStateOf("") }

    var editTarget by remember { mutableStateOf<PushNotification?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editContent by remember { mutableStateOf("") }
    var editDate by remember { mutableStateOf("") }
    var editTime by remember { mutableStateOf("") }
    val editDays = remember { mutableStateListOf<String>() }

    val scheduledItems = remember { mutableStateListOf<PushNotification>() }
    val repeatItems = remember { mutableStateListOf<PushNotification>() }

    fun resetInput() {
        title = ""
        content = ""
        time = ""
        selectedDays.clear()
    }

    fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun refresh() {
        scope.launch {
            runCatching {
                val scheduled = withContext(Dispatchers.IO) { NetworkModule.api.getScheduledNotifications() }
                val repeats = withContext(Dispatchers.IO) { NetworkModule.api.getRepeatNotifications() }
                scheduledItems.clear()
                scheduledItems.addAll(scheduled)
                repeatItems.clear()
                repeatItems.addAll(repeats)
            }.onFailure {
                toast("알림 목록을 불러오지 못했습니다: ${it.message}")
            }
        }
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    LaunchedEffect(editTarget) {
        val target = editTarget ?: return@LaunchedEffect
        editTitle = target.title
        editContent = target.body
        editDate = ""
        editTime = ""
        editDays.clear()

        if (target.type.equals("REPEAT", ignoreCase = true)) {
            val days = target.repeatDays?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
            editDays.addAll(days)
            editTime = target.repeatTime.orEmpty()
        } else {
            val parts = target.scheduledAt?.split(" ") ?: emptyList()
            if (parts.size >= 2) {
                editDate = parts[0]
                editTime = parts[1]
            }
        }
    }

    if (showLaterDialog) {
        AlertDialog(
            onDismissRequest = { showLaterDialog = false },
            confirmButton = {
                Button(onClick = {
                    if (title.isBlank() || content.isBlank()) {
                        toast("제목과 내용을 입력해 주세요.")
                        return@Button
                    }
                    if (laterDate.isBlank() || laterTime.isBlank()) {
                        toast("날짜와 시간을 선택해 주세요.")
                        return@Button
                    }
                    val schedule = "$laterDate $laterTime"
                    scope.launch {
                        runCatching {
                            withContext(Dispatchers.IO) {
                                NetworkModule.api.scheduleNotification(
                                    NotificationScheduleRequest(title, content, schedule)
                                )
                            }
                        }.onSuccess { response ->
                            if (response.isSuccessful) {
                                showLaterDialog = false
                                laterDate = ""
                                laterTime = ""
                                resetInput()
                                toast("예약 알림이 등록되었습니다.")
                                refresh()
                            } else {
                                toast("예약 등록 실패: ${response.code()}")
                            }
                        }.onFailure {
                            toast("예약 등록 실패: ${it.message}")
                        }
                    }
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
        val isRepeatEdit = editTarget?.type.equals("REPEAT", ignoreCase = true)
        AlertDialog(
            onDismissRequest = { editTarget = null },
            confirmButton = {
                Button(onClick = {
                    if (editTitle.isBlank() || editContent.isBlank()) {
                        toast("제목과 내용을 입력해 주세요.")
                        return@Button
                    }

                    scope.launch {
                        val request = if (isRepeatEdit) {
                            if (editDays.isEmpty() || editTime.isBlank()) {
                                toast("반복 요일과 시간을 선택해 주세요.")
                                return@launch
                            }
                            val repeatDays = editDays.joinToString(",")
                            NotificationUpdateRequest(
                                title = editTitle,
                                body = editContent,
                                repeatDays = repeatDays,
                                repeatTime = editTime
                            )
                        } else {
                            if (editDate.isBlank() || editTime.isBlank()) {
                                toast("날짜와 시간을 선택해 주세요.")
                                return@launch
                            }
                            val schedule = "$editDate $editTime"
                            NotificationUpdateRequest(
                                title = editTitle,
                                body = editContent,
                                scheduledAt = schedule
                            )
                        }

                        runCatching {
                            withContext(Dispatchers.IO) {
                                NetworkModule.api.updateNotification(editTarget!!.id, request)
                            }
                        }.onSuccess { response ->
                            if (response.isSuccessful) {
                                toast("알림이 수정되었습니다.")
                                editTarget = null
                                refresh()
                            } else {
                                toast("수정 실패: ${response.code()}")
                            }
                        }.onFailure {
                            toast("수정 실패: ${it.message}")
                        }
                    }
                }) { Text("수정") }
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

                    if (isRepeatEdit) {
                        Text("반복 요일", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            dayOptions.forEach { day ->
                                DayChip(
                                    label = day.label,
                                    selected = editDays.contains(day.code),
                                    onToggle = {
                                        if (editDays.contains(day.code)) editDays.remove(day.code)
                                        else editDays.add(day.code)
                                    }
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = { showTimePicker(context, editTime) { editTime = it } },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (editTime.isBlank()) "시간 선택" else editTime)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { showDatePicker(context, editDate) { editDate = it } },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (editDate.isBlank()) "날짜 선택" else editDate)
                        }
                        OutlinedButton(
                            onClick = { showTimePicker(context, editTime) { editTime = it } },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (editTime.isBlank()) "시간 선택" else editTime)
                        }
                    }
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
                                toast("제목과 내용을 입력해 주세요.")
                            } else {
                                scope.launch {
                                    runCatching {
                                        withContext(Dispatchers.IO) {
                                            NetworkModule.api.sendNotification(
                                                NotificationSendRequest(title, content)
                                            )
                                        }
                                    }.onSuccess { response ->
                                        if (response.isSuccessful) {
                                            toast("PUSH 전송이 완료되었습니다.")
                                            resetInput()
                                        } else {
                                            toast("전송 실패: ${response.code()}")
                                        }
                                    }.onFailure {
                                        toast("전송 실패: ${it.message}")
                                    }
                                }
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
                                toast("제목과 내용을 입력해 주세요.")
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
                Text("반복 요일", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    dayOptions.forEach { day ->
                        DayChip(
                            label = day.label,
                            selected = selectedDays.contains(day.code),
                            onToggle = {
                                if (selectedDays.contains(day.code)) selectedDays.remove(day.code)
                                else selectedDays.add(day.code)
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
                            toast("요일과 시간을 선택해 주세요.")
                        } else {
                            val repeatDays = selectedDays.joinToString(",")
                            scope.launch {
                                runCatching {
                                    withContext(Dispatchers.IO) {
                                        NetworkModule.api.repeatNotification(
                                            NotificationRepeatRequest(title, content, repeatDays, time)
                                        )
                                    }
                                }.onSuccess { response ->
                                    if (response.isSuccessful) {
                                        toast("반복 알림이 등록되었습니다.")
                                        resetInput()
                                        refresh()
                                    } else {
                                        toast("등록 실패: ${response.code()}")
                                    }
                                }.onFailure {
                                    toast("등록 실패: ${it.message}")
                                }
                            }
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
                Text("예약 알림이 없습니다.", color = Color.White)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(scheduledItems, key = { it.id }) { item ->
                        PushItemCard(
                            item = item,
                            onEdit = { editTarget = item },
                            onDelete = {
                                scope.launch {
                                    runCatching {
                                        withContext(Dispatchers.IO) { NetworkModule.api.deleteNotification(item.id) }
                                    }.onSuccess { response ->
                                        if (response.isSuccessful) {
                                            toast("삭제되었습니다.")
                                            refresh()
                                        } else {
                                            toast("삭제 실패: ${response.code()}")
                                        }
                                    }.onFailure {
                                        toast("삭제 실패: ${it.message}")
                                    }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("반복 알림", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (repeatItems.isEmpty()) {
                Text("반복 알림이 없습니다.", color = Color.White)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(repeatItems, key = { it.id }) { item ->
                        PushItemCard(
                            item = item,
                            onEdit = { editTarget = item },
                            onDelete = {
                                scope.launch {
                                    runCatching {
                                        withContext(Dispatchers.IO) { NetworkModule.api.deleteNotification(item.id) }
                                    }.onSuccess { response ->
                                        if (response.isSuccessful) {
                                            toast("삭제되었습니다.")
                                            refresh()
                                        } else {
                                            toast("삭제 실패: ${response.code()}")
                                        }
                                    }.onFailure {
                                        toast("삭제 실패: ${it.message}")
                                    }
                                }
                            }
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
        modifier = Modifier.clickable { onToggle() }
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
    item: PushNotification,
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
                Text(scheduleText(item), fontSize = 12.sp, color = Color.Gray)
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

private fun scheduleText(item: PushNotification): String {
    return if (item.type.equals("REPEAT", ignoreCase = true)) {
        val dayLabels = item.repeatDays
            ?.split(",")
            ?.mapNotNull { code -> dayOptions.firstOrNull { it.code == code.trim() }?.label }
            ?.joinToString(",")
            .orEmpty()
        listOf(dayLabels, item.repeatTime.orEmpty()).filter { it.isNotBlank() }.joinToString(" ")
    } else {
        item.scheduledAt.orEmpty()
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
            val formatted = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            onPicked(formatted)
        },
        initHour,
        initMinute,
        true
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
