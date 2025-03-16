package com.macieandrz.securitycamera.data.remote

import com.macieandrz.securitycamera.data.models.CrimeStatItem
import com.macieandrz.securitycamera.data.models.Location
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface CrimeStatApi {

   // https://data.police.uk/api/crimes-at-location?date=2023-02&lat=51.5073509&lng=-0.1277583

    @GET("crimes-at-location")
    suspend fun getCategory(
        @Query("date") date: String,
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double
    ) : Response<CrimeStatItem>


}

object CrimeStatRemoteSource {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://data.police.uk/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val api = retrofit.create(CrimeStatApi::class.java)
}
