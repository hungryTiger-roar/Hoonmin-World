package com.ssafy.hm.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.hm.data.model.Account

@Composable
fun SignUpScreen(
    onBack: () -> Unit,
    onSubmit: (Account) -> Unit,
    loading: Boolean
) {
    var id by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var pw2 by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var birth by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var duplicateChecked by remember { mutableStateOf(false) }
    var duplicateMessage by remember { mutableStateOf("") }
    var duplicateColor by remember { mutableStateOf(Color.Red) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "뒤로") }
                Text("회원가입", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(
                value = id,
                onValueChange = {
                    id = it
                    duplicateChecked = false
                    duplicateMessage = ""
                },
                label = { Text("아이디") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (id.isNotBlank()) {
                        duplicateChecked = true
                        duplicateMessage = "사용 가능한 아이디입니다."
                        duplicateColor = Color(0xFF2E8B57)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("아이디 중복확인")
            }
            if (duplicateMessage.isNotBlank()) {
                Text(duplicateMessage, color = duplicateColor, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = pw,
                onValueChange = { pw = it },
                label = { Text("비밀번호") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pw2,
                onValueChange = { pw2 = it },
                label = { Text("비밀번호 확인") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("이름") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = birth, onValueChange = { birth = it }, label = { Text("생년월일") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("전화번호") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    when {
                        !duplicateChecked -> duplicateMessage = "아이디 중복확인을 해주세요"
                        id.isBlank() || pw.isBlank() || pw2.isBlank() || name.isBlank() || birth.isBlank() || phone.isBlank() ->
                            duplicateMessage = "입력하지 않은 정보가 있습니다"
                        pw != pw2 -> duplicateMessage = "비밀번호를 확인해주세요"
                        else -> onSubmit(Account(id, pw, name, phone, birth, null, false))
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) { Text("회원가입 완료") }
        }
    }
}
