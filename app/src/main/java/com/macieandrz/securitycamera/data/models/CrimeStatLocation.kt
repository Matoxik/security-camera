package com.macieandrz.securitycamera.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Helper crime statistics structure for local database

@JsonClass(generateAdapter = true)
data class CrimeStatLocation(
    @Json(name = "latitude")
    val latitude: Double,
    @Json(name = "longitude")
    val longitude: Double,
)