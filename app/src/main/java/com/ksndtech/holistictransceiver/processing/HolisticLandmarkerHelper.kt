package com.ksndtech.holistictransceiver.processing

import android.content.Context
import android.os.SystemClock
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.holisticlandmarker.HolisticLandmarker
import com.google.mediapipe.tasks.vision.holisticlandmarker.HolisticLandmarkerResult

class HolisticLandmarkerHelper(
    context: Context,
    private val onResult: (HolisticLandmarkerResult, MPImage, rotationDegrees: Int) -> Unit,
    private val onError: (String) -> Unit
) {
    private val landmarker: HolisticLandmarker

    @Volatile
    private var lastRotationDegrees: Int = 0

    init {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("holistic_landmarker.task")
            .build()

        val options = HolisticLandmarker.HolisticLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setMinFaceDetectionConfidence(0.0f)  // 顔を隠すとすべて止まる挙動のワークアラウンド、止まらないがおかしな値が出ることに注意
            .setMinFaceSuppressionThreshold(0.0f)  // 同上
            .setMinFacePresenceConfidence(0.0f)  // 同上
            .setOutputFaceBlendshapes(true)
            .setResultListener { result, inputImage -> onResult(result, inputImage, lastRotationDegrees) }
            .setErrorListener { error -> onError(error.message ?: "Unknown error") }
            .build()

        landmarker = HolisticLandmarker.createFromOptions(context, options)
    }

    fun detectAsync(mpImage: MPImage, rotationDegrees: Int, timestampMs: Long) {
        lastRotationDegrees = rotationDegrees
        // val imageProcessingOptions = ImageProcessingOptions.builder()
        //     .setRotationDegrees(rotationDegrees)
        //     .build()
        // landmarker.detectAsync(mpImage, imageProcessingOptions, timestampMs)
        landmarker.detectAsync(mpImage, timestampMs)
    }

    fun close() {
        landmarker.close()
    }
}
