package com.ksndtech.holistictransceiver.camera

import android.content.Context
import android.os.SystemClock
import android.util.Log
import android.hardware.camera2.CameraCharacteristics
import androidx.concurrent.futures.await
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.ksndtech.holistictransceiver.processing.HolisticLandmarkerHelper
import com.ksndtech.holistictransceiver.ui.camera.HolisticOverlayState
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import java.util.concurrent.Executors

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

    private val analysisExecutor = Executors.newSingleThreadExecutor()
    private var holisticLandmarkerHelper: HolisticLandmarkerHelper? = null

    private val imageAnalysisUseCase = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    fun switchCamera(selector: CameraSelector) {
        _cameraSelector.value = selector
    }

    private val _overlayState = MutableStateFlow<HolisticOverlayState?>(null)
    val overlayState: StateFlow<HolisticOverlayState?> = _overlayState

    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.getInstance(appContext).await()

        // Helperとanalyzerの初期化は一度だけ。collectLatestの再実行(カメラ切替)のたびに
        // setAnalyzerを呼び直すと不整合の元になるため外に出す。
        Log.d("HolisticLandmarker", "HolisticLandmarkerHelper")
        if (holisticLandmarkerHelper == null) {
            holisticLandmarkerHelper = HolisticLandmarkerHelper(
                context = appContext,
                onResult = { result, inputImage ->
                    _overlayState.value = HolisticOverlayState(
                        faceLandmarks = result.faceLandmarks(),
                        poseLandmarks = result.poseLandmarks(),
                        leftHandLandmarks = result.leftHandLandmarks(),
                        rightHandLandmarks = result.rightHandLandmarks(),
                        imageWidth = inputImage.width,
                        imageHeight = inputImage.height,
                        isFrontCamera = cameraSelector.value == CameraSelector.DEFAULT_FRONT_CAMERA)
                },
                onError = { message -> Log.e("HolisticLandmarker", message) }
            )

            imageAnalysisUseCase.setAnalyzer(analysisExecutor) { imageProxy ->
                val bitmap = imageProxy.toBitmap()
                val mpImage = BitmapImageBuilder(bitmap).build()
                holisticLandmarkerHelper?.detectAsync(mpImage, SystemClock.uptimeMillis())
                imageProxy.close()
            }
        }

        cameraSelector.collectLatest { selector ->
            processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                selector,
                cameraPreviewUseCase,
                imageAnalysisUseCase
            )
            try {
                awaitCancellation()
            } finally {
                processCameraProvider.unbindAll()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        holisticLandmarkerHelper?.close()
        analysisExecutor.shutdown()
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
