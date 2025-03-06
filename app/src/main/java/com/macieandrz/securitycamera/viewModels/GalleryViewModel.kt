package com.macieandrz.securitycamera.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.macieandrz.securitycamera.data.models.User
import com.macieandrz.securitycamera.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GalleryViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = FirebaseRepository(app.applicationContext)
    private val _userImages = MutableStateFlow<List<String>>(emptyList())
    val userImages: StateFlow<List<String>> = _userImages


     fun loadUserImages() {
        val userId = repo.getCurrentUserId()
        if (userId != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .addSnapshotListener { snapshot, _ ->
                    val user = snapshot?.toObject(User::class.java)
                    _userImages.value = user?.images ?: emptyList()
                }
        }
    }
}
