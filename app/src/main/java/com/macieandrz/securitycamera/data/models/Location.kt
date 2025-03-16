package com.macieandrz.securitycamera.data.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "location_table")
@JsonClass(generateAdapter = true)
data class Location(

    @PrimaryKey
    var address: String,
    @Json(name = "lat")
    val lat: Double,
    @Json(name = "lng")
    val lng: Double

)