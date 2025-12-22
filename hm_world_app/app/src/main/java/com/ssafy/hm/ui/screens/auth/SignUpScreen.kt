package com.ssafy.hm.ui.screens.auth

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.safeDrawing
import com.ssafy.hm.data.model.Account
import com.ssafy.hm.ui.theme.AuroraPink
import com.ssafy.hm.ui.theme.AuroraPurple
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

@Composable
fun SignUpScreen(
    onBack: () -> Unit,
    onSubmit: (Account) -> Unit,
    onCheckId: suspend (String) -> Boolean,
    loading: Boolean
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var id by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var pw2 by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var birth by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var duplicateChecked by remember { mutableStateOf(false) }
    var duplicateMessage by remember { mutableStateOf("") }
    var duplicateColor by remember { mutableStateOf(Color.Red) }
    var checking by remember { mutableStateOf(false) }
    val calendar = remember { Calendar.getInstance() }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로 가기", tint = Color.White)
                    }
                    Text("회원가입", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Text(
                    text = "모든 항목은 필수 입력입니다.",
                    color = Color(0xFFFFB4C0),
                    fontSize = 13.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = id,
                        onValueChange = {
                            id = it
                            duplicateChecked = false
                            duplicateMessage = ""
                        },
                        label = { RequiredLabel("아이디") },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors()
                    )
                    Button(
                        onClick = {
                            if (id.isBlank()) {
                                duplicateChecked = false
                                duplicateMessage = "아이디를 입력해 주세요."
                                duplicateColor = Color.Red
                            } else {
                                checking = true
                                duplicateMessage = ""
                                scope.launch {
                                    try {
                                        val available = onCheckId(id.trim())
                                        duplicateChecked = available
                                        duplicateMessage = if (available) "사용 가능한 아이디입니다." else "이미 사용 중인 아이디입니다."
                                        duplicateColor = if (available) Color(0xFF2E8B57) else Color.Red
                                    } catch (e: Exception) {
                                        duplicateChecked = false
                                        duplicateMessage = "중복확인 중 오류가 발생했습니다."
                                        duplicateColor = Color.Red
                                        Toast.makeText(context, e.message ?: "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        checking = false
                                    }
                                }
                            }
                        },
                        enabled = !loading && !checking,
                        colors = ButtonDefaults.buttonColors(containerColor = AuroraPink.copy(alpha = 0.9f)),
                    ) {
                        Text(if (checking) "확인중..." else "중복확인")
                    }
                }
                if (duplicateMessage.isNotBlank()) {
                    Text(duplicateMessage, color = duplicateColor, fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = pw,
                    onValueChange = { pw = it },
                    label = { RequiredLabel("비밀번호") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                OutlinedTextField(
                    value = pw2,
                    onValueChange = { pw2 = it },
                    label = { RequiredLabel("비밀번호 확인") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { RequiredLabel("이름") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                OutlinedTextField(
                    value = birth,
                    onValueChange = { birth = it },
                    label = { RequiredLabel("생년월일") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val year = calendar.get(Calendar.YEAR)
                                val month = calendar.get(Calendar.MONTH)
                                val day = calendar.get(Calendar.DAY_OF_MONTH)
                                DatePickerDialog(
                                    context,
                                    { _, y, m, d ->
                                        birth = "%04d-%02d-%02d".format(y, m + 1, d)
                                        calendar.set(y, m, d)
                                    },
                                    year,
                                    month,
                                    day
                                ).show()
                            }
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "달력 열기", tint = Color.White)
                        }
                    }
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { RequiredLabel("전화번호") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        when {
                            !duplicateChecked -> {
                                duplicateMessage = "아이디 중복확인을 해주세요."
                                duplicateColor = Color.Red
                            }
                            listOf(id, pw, pw2, name, birth, phone).any { it.isBlank() } -> {
                                duplicateMessage = ""
                                Toast.makeText(context, "빈칸이 있습니다. 모두 입력해 주세요.", Toast.LENGTH_SHORT).show()
                            }
                            pw != pw2 -> {
                                duplicateMessage = "비밀번호가 일치하지 않습니다."
                                duplicateColor = Color.Red
                            }
                            else -> {
                                val birthValue = birth.trim()
                                val birthCalendar = parseBirth(birthValue)
                                val today = Calendar.getInstance().apply {
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                if (birthCalendar == null || !birthCalendar.before(today)) {
                                    Toast.makeText(context, "생년월일은 현재 날짜보다 과거여야 합니다.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val formattedPhone = formatPhone(phone.trim())
                                if (formattedPhone == null) {
                                    Toast.makeText(context, "전화번호를 010xxxxxxxx 형식으로 입력해 주세요.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                onSubmit(Account(id.trim(), pw, name.trim(), formattedPhone, birthValue, null, false))
                            }
                        }
                    },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AuroraPurple.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("회원가입 완료", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
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
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.White,
    unfocusedBorderColor = Color.Transparent,
    focusedLabelColor = AuroraPink,
    unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
    cursorColor = Color.White,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = Color.White.copy(alpha = 0.18f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.14f)
)

private fun parseBirth(value: String): Calendar? {
    return try {
        val parts = value.split("-")
        if (parts.size != 3) return null
        val y = parts[0].toInt()
        val m = parts[1].toInt() - 1
        val d = parts[2].toInt()
        Calendar.getInstance().apply {
            set(Calendar.YEAR, y)
            set(Calendar.MONTH, m)
            set(Calendar.DAY_OF_MONTH, d)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    } catch (e: Exception) {
        null
    }
}

private fun formatPhone(raw: String): String? {
    val digits = raw.filter { it.isDigit() }
    return when (digits.length) {
        11 -> String.format(Locale.US, "%s-%s-%s", digits.substring(0, 3), digits.substring(3, 7), digits.substring(7, 11))
        10 -> String.format(Locale.US, "%s-%s-%s", digits.substring(0, 3), digits.substring(3, 6), digits.substring(6, 10))
        else -> null
    }
}
