package com.ksndtech.holistictransceiver.ui.camera

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

data class HolisticOverlayState(
    val faceLandmarks: List<NormalizedLandmark>,
    val poseLandmarks: List<NormalizedLandmark>,
    val leftHandLandmarks: List<NormalizedLandmark>,
    val rightHandLandmarks: List<NormalizedLandmark>,
    val imageWidth: Int,
    val imageHeight: Int,
    val isFrontCamera: Boolean
)
