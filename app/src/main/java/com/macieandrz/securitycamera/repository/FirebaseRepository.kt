package com.macieandrz.securitycamera.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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

    private var auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun getCurrentUserId() = auth.currentUser?.uid
    fun getAuth() : FirebaseAuth = auth
    fun getStorage() = storage


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

    fun addImageToUserWithLimit(imageUrl: String) {
        val userId = getCurrentUserId() ?: return
        val userRef = fireStore.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java) ?: return@addOnSuccessListener

            // Get the current list of images or create a new one if it's empty
            val updatedImages = user.images?.toMutableList() ?: mutableListOf()
            updatedImages.add(imageUrl)

            // If the limit of 10 images is exceeded, remove the oldest one
            var oldestImageUrl: String? = null
            if (updatedImages.size > 10) {
                oldestImageUrl = updatedImages.removeAt(0)
            }

            // Update the user document with the new list of images
            userRef.update("images", updatedImages).addOnSuccessListener {
                // If an image was removed, delete it from Firebase Storage as well
                if (oldestImageUrl != null) {
                    try {
                        storage.getReferenceFromUrl(oldestImageUrl).delete()
                            .addOnSuccessListener {
                                Log.d("DEBUG", "Successfully deleted oldest photo")
                            }
                            .addOnFailureListener { e ->
                                Log.e("DEBUG", "Error deleting photo: ${e.message}")
                            }
                    } catch (e: Exception) {
                        Log.e("DEBUG", "Error getting references: ${e.message}")
                    }
                }
            }
        }
    }




}