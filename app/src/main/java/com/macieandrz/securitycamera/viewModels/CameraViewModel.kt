package com.macieandrz.securitycamera.viewModels

import MotionDetectionService
import android.app.Application
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Environment
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.UseCaseGroup
import androidx.compose.runtime.mutableStateOf
import androidx.concurrent.futures.await
import androidx.lifecycle.AndroidViewModel
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.macieandrz.securitycamera.data.models.User
import com.macieandrz.securitycamera.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class CameraViewModel(app: Application) : AndroidViewModel(app) {


    private val repo = FirebaseRepository(app.applicationContext)

    // Executors
    private val cameraExecutor: Executor = Executors.newSingleThreadExecutor()
    private val mlExecutor: Executor = Executors.newSingleThreadExecutor()

    // Pose detection
    private val poseDetectionOptions: PoseDetectorOptions = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.SINGLE_IMAGE_MODE)
        .setExecutor(mlExecutor)
        .build()
    private val poseDetector: PoseDetector = PoseDetection.getClient(poseDetectionOptions)

    // CameraX
    private val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder().build()
    private val preview: Preview = Preview.Builder().build()
    private lateinit var imageCapture: ImageCapture

    // State
    private val isHumanDetected = mutableStateOf("")
    private val detectionHistory = ArrayDeque<Boolean>(10)

    // Time tracking for saving images
    private var lastSaveTime: Long = 0
    private val saveInterval: Long = 30 * 1000 // 1 min

    private var motionDetectionService: MotionDetectionService? = null

    @OptIn(ExperimentalGetImage::class)
    fun startCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onPoseDetected: (String) -> Unit,
        motionDetectionEnabled: Boolean = false
    ) {
        // Motion detection
        if (motionDetectionService == null) {
            motionDetectionService = MotionDetectionService(
                context = context,
                coroutineScope = viewModelScope
            ) {
                // Callback triggered when motion is detected
                val currentUserId = repo.getCurrentUserId() ?: return@MotionDetectionService
                val userDoc = repo.getFirestore()
                    .collection("users")
                    .document(currentUserId)
                    .get()
                    .await()
                val user = userDoc.toObject(User::class.java)
                val friendsEmails = user?.friendsEmail ?: emptyList()

                if (friendsEmails.isNotEmpty()) {
                    repo.sendNotificationsViaFirestore(
                        friendsEmails,
                        "Wykryto ruch telefonu",
                        "Wykryto zmianę położenia kamery",
                        viewModelScope
                    )
                    Log.d("DEBUG", "Send motion notification")
                }
            }
            motionDetectionService?.initialize()
        }

        // Turn motion detection on/off
        motionDetectionService?.setEnabled(motionDetectionEnabled)

        // Camera
        viewModelScope.launch {
            val cameraProvider = ProcessCameraProvider.getInstance(context).await()
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(imageAnalysis)
                .addUseCase(preview)
                .addUseCase(imageCapture)
                .apply { previewView.viewPort?.let { setViewPort(it) } }
                .build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, useCaseGroup)
            preview.surfaceProvider = previewView.surfaceProvider

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val inputImage = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )
                    try {
                        val poseDetectorTask = poseDetector.process(inputImage)
                        val poseResult = Tasks.await(poseDetectorTask, 5L, TimeUnit.SECONDS)

                        val isHuman = poseResult.allPoseLandmarks.isNotEmpty()
                        if (detectionHistory.size >= 10) detectionHistory.removeFirst()
                        detectionHistory.addLast(isHuman)

                        val humanDetected = if (detectionHistory.count { it } > 5) {
                            "Human found"
                        } else {
                            "Human not found"
                        }

                        isHumanDetected.value = humanDetected
                        onPoseDetected(humanDetected)

                        if (humanDetected == "Human found" &&
                            System.currentTimeMillis() - lastSaveTime >= saveInterval
                        ) {
                            viewModelScope.launch(Dispatchers.IO) {
                                delay(500)
                                takePhoto(context) {
                                    repo.enqueueTasksChain() // Start background tasks after taking photo
                                }
                            }
                            lastSaveTime = System.currentTimeMillis()
                        }
                    } catch (e: Exception) {
                        Log.e("DEBUG", "Task exc: ${e.message}")
                    } finally {
                        imageProxy.close()
                    }
                } else {
                    imageProxy.close()
                }
            }
        }
    }

    private fun takePhoto(context: Context, onPhotoSaved: () -> Unit) {
        val outputDirectory =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
        val photoFile = File(outputDirectory, "photo_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d("DEBUG", "Photo saved: ${photoFile.absolutePath}")

                    // Get current friend list from firestore
                    viewModelScope.launch {
                        try {
                            val currentUserId = repo.getCurrentUserId() ?: return@launch
                            val userDoc = repo.getFirestore()
                                .collection("users")
                                .document(currentUserId)
                                .get()
                                .await()
                            val user = userDoc.toObject(User::class.java)
                            val friendsEmails = user?.friendsEmail ?: emptyList()

                            if (friendsEmails.isNotEmpty()) {
                                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                    .format(Date())

                                // Send notification
                                repo.sendNotificationsViaFirestore(
                                    friendsEmails,
                                    "Wykryto człowieka",
                                    "Wykryto osobę na posesji i wykonano zdjęcie o $timestamp",
                                    viewModelScope
                                )
                            }

                            onPhotoSaved()
                        } catch (e: Exception) {
                            Log.e("DEBUG", "Error fetching friends: ${e.message}")
                            onPhotoSaved()
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("DEBUG", "Photo capture failed: ${exception.message}")
                }
            }
        )
    }


    override fun onCleared() {
        super.onCleared()
        poseDetector.close()
        motionDetectionService?.release()
    }
}


