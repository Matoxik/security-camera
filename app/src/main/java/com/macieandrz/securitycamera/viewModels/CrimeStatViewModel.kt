package com.macieandrz.securitycamera.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.macieandrz.securitycamera.data.models.CrimeStatItem
import com.macieandrz.securitycamera.data.models.Location
import com.macieandrz.securitycamera.repository.CrimeStatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class CrimeStatViewModel(app: Application) : AndroidViewModel(app) {


    // Initialize the repository to access local and remote data
    private val repo = CrimeStatRepository(app.applicationContext)


    // MutableStateFlow to hold the location data, exposed as an immutable StateFlow
    private val _location = MutableStateFlow<Location?>(null)
    val location = _location.asStateFlow()

    // Function to fetch a single location by address
    fun performFetchSingleLocation(caseSensitiveAddress: String) = viewModelScope.launch {
        val address = caseSensitiveAddress.lowercase().trim()
        Log.d("DEBUG", "Current address: $address")
        try {
            // Attempt to fetch location from local database first
            val local = repo.getLocation(address).first()
            if (local != null) {
                Log.d("DEBUG", "Local address fetch")
                _location.update { local }
                return@launch
            }

            try {
                // If local data is not available, fetch from remote API
                val remote = repo.loadLocation(address)
                if (remote.isSuccessful) {
                    val data = remote.body()
                    Log.d("DEBUG", "Fetched geolocation data: $data")
                    if (data != null) {
                        data.address = address
                        _location.update { data }
                        repo.insertAll(listOf(data)) // Save the fetched data to the local database
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(getApplication<Application>(), "Try connect to internet",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("DEBUG", "Geolocation API Request Failed", e)
            }

        } catch (e: Exception) {
            Log.e("DEBUG", "Fetching location failed (Local or Network)", e)
        }
    }

    // MutableStateFlow to hold the crime statistics data, exposed as an immutable StateFlow
    private val _crimeStat = MutableStateFlow<List<CrimeStatItem>?>(null)
    val crimeStat = _crimeStat.asStateFlow()

    // Function to fetch crime statistics by date and coordinates
    fun performFetchSingleCrimeStat(date: String, latitude: Double, longitude: Double) = viewModelScope.launch {
        try {
            // Attempt to fetch crime data from local database first
            val local = repo.getCategory(date, latitude, longitude).first()
            if (local != null) {
                if (local.isNotEmpty()) {
                    Log.d("DEBUG", "Local crime data fetch $local")
                    _crimeStat.update { local }
                    return@launch
                }
            }

            try {
                // If local data is not available, fetch from remote API
                val remote = repo.loadCategory(date, latitude, longitude)
                if (remote.isSuccessful) {
                    val data = remote.body()
                    Log.d("DEBUG", "Fetched API crime stat data: $data")
                    if (data != null) {
                        _crimeStat.update { data }
                        repo.insertCrimeStat(data) // Save the fetched data to the local database
                    }
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "Crime stat API Request Failed", e)
            }

        } catch (e: Exception) {
            Log.e("DEBUG", "Fetching crime stats failed (Local or Network)", e)
        }
    }

    // Function to clear the local databases and reset the state
    fun clearDatabases() = viewModelScope.launch {
        try {
            repo.clearDatabases()
            _location.update { null }
            _crimeStat.update { null }
            Log.d("DEBUG", "Databases cleared successfully")
        } catch (e: Exception) {
            Log.e("DEBUG", "Cannot clear databases", e)
        }
    }
}
