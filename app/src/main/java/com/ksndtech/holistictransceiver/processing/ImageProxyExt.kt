package com.ksndtech.holistictransceiver.processing

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageProxy

fun ImageProxy.toRotatedBitmap(isFrontCamera: Boolean): Bitmap {
    val bitmap = this.toBitmap() // 既存のYUV→Bitmap変換
    val rotationDegrees = this.imageInfo.rotationDegrees
    if (rotationDegrees == 0) return bitmap

    val matrix = Matrix().apply {
        postRotate(rotationDegrees.toFloat())
        if (isFrontCamera) {
            postScale(-1f, 1f, bitmap.width.toFloat(), bitmap.height.toFloat())
        }
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}