package com.ksndtech.holistictransceiver.camera

import android.hardware.camera2.CameraCharacteristics
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraInfo
import com.ksndtech.holistictransceiver.network.CameraParamsDto

@OptIn(ExperimentalCamera2Interop::class)
fun extractCameraParams(cameraInfo: CameraInfo, frameWidth: Int, frameHeight: Int): CameraParamsDto {
    val characteristics = Camera2CameraInfo.from(cameraInfo)

    // 較正済みの内部パラメータが提供されていればそれを優先
    val intrinsic = characteristics.getCameraCharacteristic(CameraCharacteristics.LENS_INTRINSIC_CALIBRATION)
    // intrinsicの基準はpreCorrectionActiveArraySize。無ければactiveArraySizeにフォールバック
    val referenceRect = characteristics.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_SIZE)
        ?: characteristics.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
    if (intrinsic != null && referenceRect != null) {
        Log.d("CameraParam", "intrinsic found")
        val scaleX = frameWidth.toFloat() / referenceRect.width()
        val scaleY = frameHeight.toFloat() / referenceRect.height()
        // intrinsic = [fx, fy, cx, cy, skew]
        return CameraParamsDto(
            focal_length = intrinsic[0] * scaleX, // fxをスケール,
            frame_width = frameWidth,
            frame_height = frameHeight,
            cx = intrinsic[2] * scaleX,
            cy = intrinsic[3] * scaleY
        )
    }

    Log.d("CameraParam", "intrinsic not found")

    // フォールバック: 焦点距離(mm)+センサー物理サイズ(mm)から画素単位のfxを算出、pixelArraySizeではなく現在のframeWidthを使う
    val focalLengths = characteristics.getCameraCharacteristic(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
    val focalLengthMm = focalLengths?.firstOrNull() ?: 0f
    val sensorSize = characteristics.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)
    val pixelArraySize = characteristics.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)

    // val focalLengthPx = if (sensorSize != null && pixelArraySize != null && sensorSize.width > 0) {
    //     focalLengthMm * (pixelArraySize.width.toFloat() / sensorSize.width)
    // } else 0f
    val focalLengthPx = if (sensorSize != null && sensorSize.width > 0) {
        focalLengthMm * (frameWidth / sensorSize.width) // ← pixelArraySize.widthから変更
    } else 0f

    return CameraParamsDto(
        focal_length = focalLengthPx,
        frame_width = frameWidth,
        frame_height = frameHeight,
        cx = frameWidth / 2f,
        cy = frameHeight / 2f
    )
}