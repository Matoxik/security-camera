package com.macieandrz.securitycamera.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.macieandrz.securitycamera.data.models.User
import com.macieandrz.securitycamera.pages.FriendEmail
import com.macieandrz.securitycamera.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = FirebaseRepository(app.applicationContext)
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    private val _friendEmails = MutableStateFlow<List<FriendEmail>>(emptyList())
    val friendEmails: StateFlow<List<FriendEmail>> = _friendEmails

    init {
        setupAuthStateListener()
    }

    // If auth state changed update variables
    private fun setupAuthStateListener() {
        val auth = repo.getAuth()
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
          // User logged in
            if (user != null) {
                fetchFriendEmails()
            } else {
                // User logout
                _friendEmails.value = emptyList()
            }
        }
        auth.addAuthStateListener(authStateListener!!)
    }

    override fun onCleared() {
        super.onCleared()
        authStateListener?.let {
            repo.getAuth().removeAuthStateListener(it)
        }
    }


    private fun fetchFriendEmails() {
        viewModelScope.launch {
            try {
                val currentUserId = repo.getCurrentUserId() ?: return@launch
                val userDoc = repo.getFirestore().collection("users").document(currentUserId).get().await()
                val user = userDoc.toObject(User::class.java)
                val emails = user?.friendsEmail ?: emptyList()
                _friendEmails.value = emails.map { FriendEmail(it) }
            } catch (e: Exception) {
                Log.e("DEBUG", "NotificationViewModel fetchFriendEmails() failed: ${e.message}")
            }
        }
    }

    fun addFriendEmail(email: FriendEmail) {

        if (_friendEmails.value.any { it.email == email.email }) {
            return
        }

        val currentEmails = _friendEmails.value.toMutableList()
        currentEmails.add(email)
        _friendEmails.value = currentEmails

        updateFriendEmailsInFirestore()
    }

    fun removeFriendEmail(email: FriendEmail) {
        val currentEmails = _friendEmails.value.toMutableList()
        currentEmails.removeIf { it.email == email.email }
        _friendEmails.value = currentEmails

        updateFriendEmailsInFirestore()
    }
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
}

