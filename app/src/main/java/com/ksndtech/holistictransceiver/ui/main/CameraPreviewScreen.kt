package com.ksndtech.holistictransceiver.ui.main

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraInfo
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

import com.ksndtech.holistictransceiver.camera.CameraPreviewViewModel
import com.ksndtech.holistictransceiver.camera.displayName
import com.ksndtech.holistictransceiver.camera.toCameraSelector
import com.ksndtech.holistictransceiver.ui.camera.HolisticOverlay

@Composable
fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraPreviewViewModel = viewModel()
) {
    val surfaceRequest by viewModel.surfaceRequest.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var availableCameras by remember { mutableStateOf<List<CameraInfo>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }

    val overlayState by viewModel.overlayState.collectAsState()

    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }

    LaunchedEffect(Unit) {
        availableCameras = viewModel.getAvailableCameras(context.applicationContext)
    }

    Box(modifier = modifier) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = Modifier.fillMaxSize()
            )
        }
        overlayState?.let { state ->
            HolisticOverlay(state = state, modifier = Modifier.fillMaxSize())
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Button(onClick = { expanded = true }) {
                Text("カメラ選択")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableCameras.forEach { cameraInfo ->
                    DropdownMenuItem(
                        text = { Text(cameraInfo.displayName()) },
                        onClick = {
                            viewModel.switchCamera(cameraInfo.toCameraSelector())
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
