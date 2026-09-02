package com.ksndtech.holistictransceiver

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.ksndtech.holistictransceiver.ui.main.CameraPreviewScreen
import com.ksndtech.holistictransceiver.ui.settings.SettingsScreen
import com.ksndtech.holistictransceiver.ui.theme.HolisticTransceiverTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("HolisticLandmarker", "MainActivity")

        enableEdgeToEdge()
        setContent {
            HolisticTransceiverTheme {
                    AppRoot()
            }
        }
    }
}

sealed class Screen {
    object Camera : Screen()
    object Settings : Screen()
}

@Composable
fun AppRoot() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Camera) }
    val context = androidx.compose.ui.platform.LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // 初回起動時に権限をリクエスト
    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
//        CameraPreviewScreen(modifier = Modifier.fillMaxSize())
//        CameraPreviewScreen()
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                Screen.Camera -> CameraPreviewScreen(modifier = Modifier.fillMaxSize())
                Screen.Settings -> SettingsScreen(modifier = Modifier.fillMaxSize())
            }

            IconButton(
                onClick = {
                    currentScreen = if (currentScreen == Screen.Camera) Screen.Settings else Screen.Camera
                },
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "設定")
            }
        }
    } else {
        Text("カメラ権限が必要です")
    }
}