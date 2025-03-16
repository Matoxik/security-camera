package com.macieandrz.securitycamera.repository

import android.content.Context
import android.util.Log
import com.macieandrz.securitycamera.data.local.LocationDb
import com.macieandrz.securitycamera.data.models.Location
import com.macieandrz.securitycamera.data.remote.RemoteSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Response

class CrimeStatRepository(context: Context) {

    private val dao = LocationDb.getInstance(context).locationDao()
    private val api = RemoteSource.api

    // Get data from api
    suspend fun loadLocation(address: String): Response<Location> {
        return api.getLocation(address)
    }


    // Get data from local database
    fun getLocation(address: String): Flow<Location?> {
        return dao.getLocation(address)
    }

    suspend fun insertAll(list: List<Location>) = withContext(Dispatchers.IO) {
        dao.insert(list)
    }


}