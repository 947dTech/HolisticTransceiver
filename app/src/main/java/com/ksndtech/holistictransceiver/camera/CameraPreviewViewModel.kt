package com.ksndtech.holistictransceiver.camera

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import androidx.concurrent.futures.await
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

class CameraPreviewViewModel : ViewModel() {
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.value = newSurfaceRequest
        }
    }

    private val _cameraSelector = MutableStateFlow(CameraSelector.DEFAULT_BACK_CAMERA)
    val cameraSelector: StateFlow<CameraSelector> = _cameraSelector

    fun switchCamera(selector: CameraSelector) {
        _cameraSelector.value = selector
    }

    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.getInstance(appContext).await()

        cameraSelector.collectLatest { selector ->
            processCameraProvider.bindToLifecycle(
                lifecycleOwner, selector, cameraPreviewUseCase
            )
            try {
                awaitCancellation()
            } finally {
                processCameraProvider.unbindAll()
            }
        }
    }

    suspend fun getAvailableCameras(appContext: Context): List<CameraInfo> {
        val provider = ProcessCameraProvider.getInstance(appContext).await()
        return provider.availableCameraInfos
    }
}

@OptIn(ExperimentalCamera2Interop::class)
fun CameraInfo.toCameraSelector(): CameraSelector {
    val cameraId = Camera2CameraInfo.from(this).cameraId
    return CameraSelector.Builder()
        .addCameraFilter { infos -> infos.filter { Camera2CameraInfo.from(it).cameraId == cameraId } }
        .build()
}

@OptIn(ExperimentalCamera2Interop::class)
fun CameraInfo.displayName(): String {
    val characteristics = Camera2CameraInfo.from(this)
    val facing = characteristics.getCameraCharacteristic(CameraCharacteristics.LENS_FACING)
    val facingLabel = when (facing) {
        CameraCharacteristics.LENS_FACING_FRONT -> "前面"
        CameraCharacteristics.LENS_FACING_BACK -> "背面"
        else -> "外部"
    }
    val cameraId = characteristics.cameraId
    return "$facingLabel (ID: $cameraId)"
}
