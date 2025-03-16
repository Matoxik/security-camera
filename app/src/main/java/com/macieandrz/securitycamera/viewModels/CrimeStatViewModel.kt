package com.macieandrz.securitycamera.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.macieandrz.securitycamera.data.models.Location
import com.macieandrz.securitycamera.pages.CrimeStatRoute
import com.macieandrz.securitycamera.repository.CrimeStatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CrimeStatViewModel(app: Application) : AndroidViewModel(app) {

private val repo = CrimeStatRepository(app.applicationContext)

    private val _location = MutableStateFlow<Location?>(null)
    val location = _location.asStateFlow()


    fun performFetchSingleLocation(address: String) = viewModelScope.launch {
        try {
            val local = repo.getLocation(address).first()

            if (local != null) {
                _location.update { local }
                return@launch
            }

            try {
                val remote = repo.loadLocation(address)
                if (remote.isSuccessful){
                    val data = remote.body()
                    Log.d("DEBUG", "Fetched data: $data")
                    if (data != null) {
                        data.address = address
                        _location.update { data }
                        repo.insertAll(listOf(data))
                    }
                }
            } catch (e: Exception){
                Log.e("DEBUG", "API Request Failed", e)
            }


         } catch (e: Exception) {
            Log.e("PROCESS_D", "Fetching location failed (Local or Network)", e)
         }

    }

}
