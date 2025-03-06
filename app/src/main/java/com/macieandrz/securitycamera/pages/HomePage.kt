package com.macieandrz.securitycamera.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.macieandrz.securitycamera.viewModels.AuthState
import com.macieandrz.securitycamera.viewModels.AuthViewModel
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Composable
fun HomePage(modifier: Modifier = Modifier,
             navController: NavController,
             authViewModel: AuthViewModel
) {
   val authState = authViewModel.authState.collectAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
           is AuthState.UnAuthenticated -> navController.navigate(LoginRoute)
            else -> Unit
        }
    }

    Column(modifier = modifier.fillMaxSize(),
       verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Text(text = "Home Page", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        //Go to camera page
        Button(
            onClick = {
           navController.navigate(CameraRoute)
            }
        ) {
            Text(
                text = "Turn on security camera",
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Go to database page
        Button(
            onClick = {

            }
        ) {
            Text(
                text = "Gallery",
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))


        TextButton(onClick = {
        authViewModel.signout()
    }) {
        Text(text = "Sign out")
    }

    }



}