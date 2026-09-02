package com.ksndtech.holistictransceiver.network

import kotlinx.serialization.Serializable

@Serializable
data class LandmarkDto(
    val x: Float,
    val y: Float,
    val z: Float
)

@Serializable
data class HolisticFrameDto(
    val pose_landmarks_stamp: Long, // 取得時刻(epoch ns)
    val pose_landmarks: List<LandmarkDto>,
    val pose_world_landmarks_stamp: Long,
    val pose_world_landmarks: List<LandmarkDto>,
    val face_landmarks_stamp: Long,
    val face_landmarks: List<LandmarkDto>,
    val face_blendshapes: Map<String, Float>,
    val left_hand_landmarks_stamp: Long,
    val left_hand_landmarks: List<LandmarkDto>,
    val left_hand_world_landmarks_stamp: Long,
    val left_hand_world_landmarks: List<LandmarkDto>,
    val right_hand_landmarks_stamp: Long,
    val right_hand_landmarks: List<LandmarkDto>,
    val right_hand_world_landmarks_stamp: Long,
    val right_hand_world_landmarks: List<LandmarkDto>
)