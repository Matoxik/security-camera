package com.macieandrz.securitycamera.viewModels

import androidx.lifecycle.ViewModel
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.content.Context
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
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
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
    val isHumanDetected = mutableStateOf("")
    val detectionHistory = ArrayDeque<Boolean>(10)


    @OptIn(ExperimentalGetImage::class)
    fun startCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onPoseDetected: (String) -> Unit
    ) {
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

            preview.setSurfaceProvider(previewView.surfaceProvider)

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


    override fun onCleared() {
        super.onCleared()
        poseDetector.close()
    }
}