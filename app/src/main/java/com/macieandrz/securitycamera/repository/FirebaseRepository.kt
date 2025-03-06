package com.macieandrz.securitycamera.repository

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.macieandrz.securitycamera.data.models.User


class FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    private val userId = auth.currentUser?.uid


    fun getAuth() : FirebaseAuth = auth
    fun getCurrentUserId() = userId

    fun createNewUser(user: User) {
        fireStore.collection("users")
    .document(user.uid!!)
    .set(user)

    }



}