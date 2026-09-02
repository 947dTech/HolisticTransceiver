package com.ksndtech.holistictransceiver.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()

    var hostInput by remember { mutableStateOf("") }
    var portInput by remember { mutableStateOf("") }

    // 保存済みの値が読み込まれたら入力欄の初期値として反映
    LaunchedEffect(settings) {
        hostInput = settings.host
        portInput = settings.port.toString()
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("UDP送信先設定")

        OutlinedTextField(
            value = hostInput,
            onValueChange = { hostInput = it },
            label = { Text("送信先IPアドレス") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = portInput,
            onValueChange = { portInput = it.filter { c -> c.isDigit() } },
            label = { Text("ポート番号") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val port = portInput.toIntOrNull()
                if (port != null && port in 1..65535) {
                    viewModel.save(hostInput, port)
                }
            }
        ) {
            Text("保存")
        }
    }
}