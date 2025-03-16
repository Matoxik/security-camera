package com.macieandrz.securitycamera.data.models


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "crime_stat_table")
@JsonClass(generateAdapter = true)
data class CrimeStatItem(
    @Json(name = "category")
    val category: String,

    @PrimaryKey
    @Json(name = "id")
    val id: Int,

    @Embedded
    @Json(name = "location")
    val location: CrimeStatLocation,

    @Json(name = "month")
    val month: String,
)
