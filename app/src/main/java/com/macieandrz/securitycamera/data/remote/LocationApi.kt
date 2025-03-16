package com.macieandrz.securitycamera.data.remote

import GeocodingAdapter
import com.macieandrz.securitycamera.data.models.Location
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface LocationApi {

    //https://maps.googleapis.com/maps/api/geocode/json?
    //address=London,UK&key=AIzaSyCr_eltKVyLAw8y3vyr8mvx8DxyTeI0xjs

    @GET("json")
    suspend fun getLocation(
        @Query("address") address: String,
        @Query("key") key: String = "AIzaSyCr_eltKVyLAw8y3vyr8mvx8DxyTeI0xjs"
    ) :Response<Location>


}

object RemoteSource {
    private val moshi = Moshi.Builder()
        .add(GeocodingAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api = retrofit.create(LocationApi::class.java)
}
