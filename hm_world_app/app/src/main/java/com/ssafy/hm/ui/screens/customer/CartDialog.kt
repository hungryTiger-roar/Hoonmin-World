package com.ssafy.hm.ui.screens.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ssafy.hm.data.model.Item

@Composable
fun CartDialog(
    cart: Map<Item, Int>,
    onChange: (Item, Int) -> Unit,
    onOrder: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var store by remember { mutableStateOf("1") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val storeId = store.toIntOrNull() ?: 1
                onOrder(storeId)
            }) { Text("주문") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("닫기") } },
        title = { Text("장바구니") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (cart.isEmpty()) {
                    Text("장바구니가 비었어요")
                } else {
                    cart.forEach { (item, qty) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.itemName)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onChange(item, qty - 1) }) { Text("-") }
                                Text("$qty")
                                IconButton(onClick = { onChange(item, qty + 1) }) { Text("+") }
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = store,
                    onValueChange = { store = it },
                    label = { Text("수령 매장 번호 (1/2/3)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    )
}
