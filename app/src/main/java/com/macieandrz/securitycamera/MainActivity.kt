package com.macieandrz.securitycamera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.macieandrz.securitycamera.ui.theme.SecurityCameraTheme
import com.macieandrz.securitycamera.viewModels.AuthViewModel
import com.macieandrz.securitycamera.viewModels.CameraViewModel
import com.macieandrz.securitycamera.viewModels.CrimeStatViewModel
import com.macieandrz.securitycamera.viewModels.GalleryViewModel
import com.macieandrz.securitycamera.viewModels.NotificationViewModel


class MainActivity : ComponentActivity() {
    private val authViewModel by viewModels<AuthViewModel>()
    private val cameraViewModel by viewModels<CameraViewModel>()
    private val galleryViewModel by viewModels<GalleryViewModel>()
    private val notificationViewModel by viewModels<NotificationViewModel>()
    private val crimeStatViewModel by viewModels<CrimeStatViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecurityCameraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    MyAppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel,
                        cameraViewModel = cameraViewModel,
                        galleryViewModel = galleryViewModel,
                        notificationViewModel = notificationViewModel,
                        crimeStatViewModel = crimeStatViewModel
                    )
                }
                }
            }
        }
    }
}
