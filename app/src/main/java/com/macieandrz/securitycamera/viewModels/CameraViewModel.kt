package com.macieandrz.securitycamera.viewModels

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.content.Context
import android.media.Image
import android.os.Environment
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.mutableStateOf
import androidx.concurrent.futures.await
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

class CameraViewModel : ViewModel() {

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

    // State
    private val isHumanDetected = mutableStateOf("")
    private val detectionHistory = ArrayDeque<Boolean>(10)

    // File storage
    private lateinit var outputDirectory: File

    // Time tracking for saving images
    private var lastSaveTime: Long = 0
    private val saveInterval: Long = 60 * 1000 // 1 min

    @OptIn(ExperimentalGetImage::class)
    fun startCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onPoseDetected: (String) -> Unit
    ) {
        outputDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir

        viewModelScope.launch {
            val cameraProvider = ProcessCameraProvider.getInstance(context).await()
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            val useCaseGroup = androidx.camera.core.UseCaseGroup.Builder()
                .addUseCase(imageAnalysis)
                .addUseCase(preview)
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

                        // Process the pose result
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

                        // Capture and save image if human is detected
                        if (isHumanDetected.value == "Human found"
                            && System.currentTimeMillis() - lastSaveTime >= saveInterval) {
                            saveImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
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

    // Save image to local memory
    private fun saveImage(mediaImage: Image, rotationDegrees: Int) {
        val buffer = mediaImage.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val file = File(outputDirectory, "${System.currentTimeMillis()}.jpg")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                FileOutputStream(file).use { output ->
                    output.write(bytes)
                }
                Log.d("DEBUG", "photo_${System.currentTimeMillis()}.jpg")
            } catch (e: IOException) {
                Log.e("DEBUG", "Failed to save image: ${e.message}")
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        poseDetector.close()
    }
}
