package com.macieandrz.securitycamera.data.remote

import com.macieandrz.securitycamera.data.models.CrimeStatItem
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface CrimeStatApi {

   // https://data.police.uk/api/crimes-at-location?date=2023-02&lat=51.5073509&lng=-0.1277583

    // Request to api using retrofit
    @GET("crimes-at-location")
    suspend fun getCategory(
        @Query("date") date: String,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ) : Response<List<CrimeStatItem>>


}

object CrimeStatRemoteSource {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://data.police.uk/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val api = retrofit.create(CrimeStatApi::class.java)
}
