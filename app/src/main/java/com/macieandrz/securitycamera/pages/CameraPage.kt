package com.macieandrz.securitycamera.pages

import android.Manifest
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.concurrent.futures.await
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetector
import com.macieandrz.securitycamera.viewModels.AuthViewModel
import com.macieandrz.securitycamera.viewModels.CameraViewModel
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

@Serializable
object CameraRoute

@kotlin.OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    cameraViewModel: CameraViewModel
) {
    val cameraPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.CAMERA)
    )
    var isCountdownFinished by remember { mutableStateOf(false) }

    Home(modifier = modifier, navController)

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!cameraPermissionsState.allPermissionsGranted) {
            Column {
                Button(
                    onClick = { cameraPermissionsState.launchMultiplePermissionRequest() },
                ) {
                    Text(text = "Ask for permissions")
                }
            }
        }

        if (!isCountdownFinished) {
            AnimatedCounter(
                startValue = 10, // In seconds
                style = MaterialTheme.typography.displayLarge,
                onCountdownFinished = {
                    isCountdownFinished = true
                }
            )
        }
    }

    if (cameraPermissionsState.allPermissionsGranted && isCountdownFinished) {
        Box(modifier = Modifier.fillMaxSize()) {
            var isHumanDetected by remember { mutableStateOf("") }

            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val previewView = remember { PreviewView(context) }

            LaunchedEffect(Unit) {
                cameraViewModel.startCamera(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    previewView = previewView,
                    onPoseDetected = { detected ->
                        isHumanDetected = detected
                    }
                )
            }

            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Card(
                    modifier = Modifier.wrapContentSize(),
                    elevation = CardDefaults.elevatedCardElevation(5.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(30.dp),
                        text = isHumanDetected,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Home(modifier = modifier, navController)
        }
    }
}

@Composable
fun AnimatedCounter(
    startValue: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displayLarge,
    onCountdownFinished: () -> Unit
) {
    var currentCount by remember { mutableIntStateOf(startValue) }
    var oldCount by remember { mutableIntStateOf(startValue) }

    LaunchedEffect(key1 = currentCount) {
        if (currentCount > 0) {
            delay(1000)
            oldCount = currentCount
            currentCount--
        } else {
            onCountdownFinished()
        }
    }

    Row(modifier = modifier) {
        val countString = currentCount.toString()
        val oldCountString = oldCount.toString()

        for (i in countString.indices) {
            val oldChar = oldCountString.getOrNull(i)
            val newChar = countString[i]
            val char = if (oldChar == newChar) {
                oldCountString[i]
            } else {
                countString[i]
            }
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    slideInVertically { it } togetherWith slideOutVertically { -it }
                }
            ) { char ->
                Text(
                    text = char.toString(),
                    style = style,
                    softWrap = false
                )
            }
        }
    }
}


@Composable
fun Home(modifier: Modifier = Modifier,  navController: NavController) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Card(
            modifier = Modifier.wrapContentSize(),
            elevation = CardDefaults.elevatedCardElevation(5.dp)
        ) {
            TextButton(onClick = {
                navController.navigate(HomeRoute)
            }) {
                Text(
                    text = "Home",
                    fontSize = 20.sp
                )
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Back to home"
                )
            }
        }
    }
}




