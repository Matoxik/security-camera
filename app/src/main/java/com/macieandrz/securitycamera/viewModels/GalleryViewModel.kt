package com.macieandrz.securitycamera.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.macieandrz.securitycamera.data.models.User
import com.macieandrz.securitycamera.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GalleryViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = FirebaseRepository(app.applicationContext)
    private val auth = FirebaseAuth.getInstance()

    private val _userImages = MutableStateFlow<List<String>>(emptyList())
    val userImages: StateFlow<List<String>> = _userImages

    // Stores a reference to the listener
    private var listenerRegistration: ListenerRegistration? = null

    init {
        // Observe authentication state changes
        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                loadUserImages()
            } else {
                // Clear images and remove the listener when the user is logged out
                clearUserImagesAndListener()
            }
        }
    }

    private fun clearUserImagesAndListener() {
        _userImages.value = emptyList()
        // Remove the listener if it exists
        listenerRegistration?.remove()
        listenerRegistration = null
    }

    private fun loadUserImages() {
        // Remove the previous listener if it exists
        listenerRegistration?.remove()

        val userId = repo.getCurrentUserId()
        Log.d("DEBUG", "Current user id: ${userId}")
        if (userId != null) {
            // Save a reference to the new listener
            listenerRegistration = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .addSnapshotListener { snapshot, _ ->
                    val user = snapshot?.toObject(User::class.java)
                    _userImages.value = user?.images ?: emptyList()
                }
        } else {
            // If there is no userId, clear the images
            clearUserImagesAndListener()
        }
    }

    // Important: Clear the listener when the ViewModel is destroyed
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
