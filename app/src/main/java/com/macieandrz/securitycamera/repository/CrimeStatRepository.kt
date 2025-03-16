package com.macieandrz.securitycamera.repository

import android.content.Context
import com.macieandrz.securitycamera.data.local.CrimeStatDb
import com.macieandrz.securitycamera.data.local.LocationDb
import com.macieandrz.securitycamera.data.models.CrimeStatItem
import com.macieandrz.securitycamera.data.models.Location
import com.macieandrz.securitycamera.data.remote.CrimeStatRemoteSource
import com.macieandrz.securitycamera.data.remote.RemoteSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Response

class CrimeStatRepository(context: Context) {


    // Geolocation
    private val locationDao = LocationDb.getInstance(context).locationDao()
    private val locationApi = RemoteSource.api

    // Get data from api
    suspend fun loadLocation(address: String): Response<Location> {
        return locationApi.getLocation(address)
    }


    // Get data from local database
    fun getLocation(address: String): Flow<Location?> {
        return locationDao.getLocation(address)
    }

    suspend fun insertAll(list: List<Location>) = withContext(Dispatchers.IO) {
        locationDao.insert(list)
    }


    // Crime Stat
    private val crimeStatDao = CrimeStatDb.getInstance(context).crimeStatDao()
    private val crimeStatApi = CrimeStatRemoteSource.api


    // Get data from api
    suspend fun loadCategory(date: String, latitude: Double, longitude: Double): Response<CrimeStatItem> {
        return crimeStatApi.getCategory(date, latitude, longitude)
    }

    // Get data from local database
    fun getCategory(date: String, latitude: Double, longitude: Double) : Flow<CrimeStatItem?>{
        return crimeStatDao.getCategory(date, latitude, longitude)
    }

    suspend fun insertCrimeStat(list: List<CrimeStatItem>) = withContext(Dispatchers.IO) {
        crimeStatDao.insert(list)
    }

}