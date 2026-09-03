package com.ksndtech.holistictransceiver.network

import kotlinx.serialization.Serializable

@Serializable
data class CameraParamsDto(
    val focal_length: Float,
    val frame_width: Int,
    val frame_height: Int,
    val cx: Float,
    val cy: Float
)