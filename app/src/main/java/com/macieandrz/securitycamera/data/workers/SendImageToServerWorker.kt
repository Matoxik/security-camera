package com.macieandrz.securitycamera.data.workers

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.macieandrz.securitycamera.data.models.User
import com.macieandrz.securitycamera.repository.FirebaseRepository
import kotlinx.coroutines.tasks.await

class SendImageToServerWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    private val inputDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
    private val repository = FirebaseRepository(context)

    override fun doWork(): Result {
        val files = inputDirectory.listFiles()
        val userId = repository.getCurrentUserId() ?: return Result.failure()

        var success = true

        files?.forEach { file ->
            if (file.name.startsWith("photo_") && file.name.endsWith(".jpg")) {
                val storageRef = repository.getStorage().reference
                val imageRef = storageRef.child("images/${file.name}")
                val uploadTask = imageRef.putFile(Uri.fromFile(file))

                uploadTask.addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
                        userRef.get().addOnSuccessListener { document ->
                            val user = document.toObject(User::class.java)
                            val updatedImages = user?.images?.toMutableList() ?: mutableListOf()
                            updatedImages.add(imageUrl)
                            userRef.update("images", updatedImages)
                        }
                    }
                }.addOnFailureListener {
                    success = false
                }
            }
        }

        Log.d("DEBUG", "Send image to server completed")
        return if (success) Result.success() else Result.retry()
    }
}
