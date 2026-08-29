package com.ksndtech.holistictransceiver.ui.camera

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

@Composable
fun HolisticOverlay(state: HolisticOverlayState, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val imageWidth = state.imageWidth.toFloat()
        val imageHeight = state.imageHeight.toFloat()

        // center-crop(FILL)前提のスケール計算。Viewfinder側のContentScaleと揃える必要あり
        val scale = maxOf(size.width / imageWidth, size.height / imageHeight)
        val offsetX = (size.width - imageWidth * scale) / 2f
        val offsetY = (size.height - imageHeight * scale) / 2f

        fun NormalizedLandmark.toOffset(): Offset {
            val px = x() * imageWidth * scale + offsetX
            val py = y() * imageHeight * scale + offsetY
            val mirroredX = if (state.isFrontCamera) size.width - px else px
            return Offset(mirroredX, py)
        }

        fun drawPoints(landmarks: List<NormalizedLandmark>, color: Color) {
            landmarks.forEach { drawCircle(color = color, radius = 4.dp.toPx(), center = it.toOffset()) }
        }

        drawPoints(state.faceLandmarks, Color.Yellow)
        drawPoints(state.poseLandmarks, Color.Green)
        drawPoints(state.leftHandLandmarks, Color.Cyan)
        drawPoints(state.rightHandLandmarks, Color.Magenta)
    }
}