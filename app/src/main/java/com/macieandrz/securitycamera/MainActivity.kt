package com.macieandrz.securitycamera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.macieandrz.securitycamera.ui.theme.SecurityCameraTheme

class MainActivity : ComponentActivity() {
   val authViewModel : AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecurityCameraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                     MyAppNavigation(
                         modifier = Modifier.padding(innerPadding),
                         authViewModel = authViewModel
                     )
                }
            }
        }
    }
}
