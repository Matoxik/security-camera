package com.macieandrz.securitycamera.data.models



data class User(
    val uid: String? = null,
    val email: String? = null,
    val images: List<String>? = null,
    val fcmToken: String? = null,
    val friendsEmail: List<String>? = null,
    val motionDetectionEnabled: Boolean = false
)
