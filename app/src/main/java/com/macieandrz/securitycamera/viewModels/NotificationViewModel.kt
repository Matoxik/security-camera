package com.macieandrz.securitycamera.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.macieandrz.securitycamera.data.models.User
import com.macieandrz.securitycamera.pages.FriendEmail
import com.macieandrz.securitycamera.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationViewModel(app: Application) : AndroidViewModel(app) {
    // Initialize repository and auth state listener
    private val repo = FirebaseRepository(app.applicationContext)
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    // MutableStateFlow to hold friend emails and motion detection status
    private val _friendEmails = MutableStateFlow<List<FriendEmail>>(emptyList())
    val friendEmails: StateFlow<List<FriendEmail>> = _friendEmails

    private val _isMotionDetectionEnabled = MutableStateFlow(false)
    val isMotionDetectionEnabled: StateFlow<Boolean> = _isMotionDetectionEnabled

    init {
        setupAuthStateListener()
    }

    // Set up Firebase AuthStateListener to track user login/logout
    private fun setupAuthStateListener() {
        val auth = repo.getAuth()
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            // User logged in
            if (user != null) {
                fetchFriendEmails()
            } else {
                // User logged out
                _friendEmails.value = emptyList()
            }
        }
        auth.addAuthStateListener(authStateListener!!)
    }

    // Remove the auth state listener when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        authStateListener?.let {
            repo.getAuth().removeAuthStateListener(it)
        }
    }

    // Fetch friend emails from Firestore
    private fun fetchFriendEmails() {
        viewModelScope.launch {
            try {
                val currentUserId = repo.getCurrentUserId() ?: return@launch
                val userDoc = repo.getFirestore().collection("users").document(currentUserId).get().await()
                val user = userDoc.toObject(User::class.java)
                val emails = user?.friendsEmail ?: emptyList()
                _friendEmails.value = emails.map { FriendEmail(it) }
                _isMotionDetectionEnabled.value = user?.motionDetectionEnabled ?: false
            } catch (e: Exception) {
                Log.e("DEBUG", "NotificationViewModel fetchFriendEmails() failed: ${e.message}")
            }
        }
    }

    // Add a friend email to the list and update Firestore
    fun addFriendEmail(email: FriendEmail) {
        if (_friendEmails.value.any { it.email == email.email }) {
            return
        }

        val currentEmails = _friendEmails.value.toMutableList()
        currentEmails.add(email)
        _friendEmails.value = currentEmails

        updateFriendEmailsInFirestore()
    }

    // Remove a friend email from the list and update Firestore
    fun removeFriendEmail(email: FriendEmail) {
        val currentEmails = _friendEmails.value.toMutableList()
        currentEmails.removeIf { it.email == email.email }
        _friendEmails.value = currentEmails

        updateFriendEmailsInFirestore()
    }

    // Update friend emails in Firestore
    private fun updateFriendEmailsInFirestore() {
        viewModelScope.launch {
            val emailStrings = _friendEmails.value.map { it.email }
            val userUpdate = User(
                uid = repo.getCurrentUserId(),
                friendsEmail = emailStrings
            )

            repo.updateUserFriendList(userUpdate)
        }
    }

    // Enable or disable motion detection and update Firestore
    fun setMotionDetectionEnabled(enabled: Boolean) {
        _isMotionDetectionEnabled.value = enabled
        updateMotionDetectionSetting()
    }

    // Update motion detection setting in Firestore
    private fun updateMotionDetectionSetting() {
        viewModelScope.launch {
            try {
                val currentUserId = repo.getCurrentUserId() ?: return@launch
                repo.getFirestore().collection("users").document(currentUserId)
                    .update("motionDetectionEnabled", _isMotionDetectionEnabled.value)
                    .await()
            } catch (e: Exception) {
                Log.e("DEBUG", "Motion detection setting update error: ${e.message}")
            }
        }
    }
}
