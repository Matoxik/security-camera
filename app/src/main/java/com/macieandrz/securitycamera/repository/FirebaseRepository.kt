package com.macieandrz.securitycamera.repository

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.macieandrz.securitycamera.data.models.User
import com.macieandrz.securitycamera.data.workers.ClearCacheWorker
import com.macieandrz.securitycamera.data.workers.SendImageToServerWorker


class FirebaseRepository(context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val userId = auth.currentUser?.uid


    fun getAuth() : FirebaseAuth = auth
    fun getStorage() = storage
    fun getCurrentUserId() = userId

    fun createNewUser(user: User) {
        fireStore.collection("users")
    .document(user.uid!!)
    .set(user)

    }

    // Workers
    private val workManager = WorkManager.getInstance(context)

    fun enqueueTasksChain() {
        val networkConstants = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        val firstTask = OneTimeWorkRequestBuilder<SendImageToServerWorker>()
            .setConstraints(networkConstants)
            .build()

        val secondTask = OneTimeWorkRequestBuilder<ClearCacheWorker>()
            .build()

        workManager.beginUniqueWork(
            "SEND_IMAGE_TO_STORAGE",
            ExistingWorkPolicy.REPLACE,
            firstTask
        ).then(secondTask)
            .enqueue()
    }


}