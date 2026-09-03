package com.ksndtech.holistictransceiver.network

import com.google.mediapipe.tasks.components.containers.Category
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.components.containers.Landmark
import com.google.mediapipe.tasks.vision.holisticlandmarker.HolisticLandmarkerResult

fun HolisticLandmarkerResult.toFrameDto(
    stampNs: Long,
    gravity: FloatArray,
    cameraParams: CameraParamsDto
): HolisticFrameDto {
    fun List<Category>.toBlendshapeMap(): Map<String, Float> =
        associate { it.categoryName() to it.score() }
    fun List<NormalizedLandmark>.toDtoList() = map { LandmarkDto(it.x(), it.y(), it.z()) }
    fun List<Landmark>.toDtoList() = map { LandmarkDto(it.x(), it.y(), it.z()) }

    return HolisticFrameDto(
        pose_landmarks_stamp = stampNs,
        pose_landmarks = poseLandmarks().toDtoList(),
        pose_world_landmarks_stamp = stampNs,
        pose_world_landmarks = poseWorldLandmarks().toDtoList(),
        face_landmarks_stamp = stampNs,
        face_landmarks = faceLandmarks().toDtoList(),
        face_blendshapes = faceBlendshapes().orElse(emptyList()).toBlendshapeMap(),
        left_hand_landmarks_stamp = stampNs,
        left_hand_landmarks = leftHandLandmarks().toDtoList(),
        left_hand_world_landmarks_stamp = stampNs,
        left_hand_world_landmarks = leftHandWorldLandmarks().toDtoList(),
        right_hand_landmarks_stamp = stampNs,
        right_hand_landmarks = rightHandLandmarks().toDtoList(),
        right_hand_world_landmarks_stamp = stampNs,
        right_hand_world_landmarks = rightHandWorldLandmarks().toDtoList(),
        gravity = gravity.toList(),
        camera_params = cameraParams
    )
}