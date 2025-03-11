package com.macieandrz.securitycamera.pages

import android.Manifest
import android.os.Build
import android.util.Patterns
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.macieandrz.securitycamera.viewModels.AuthViewModel
import com.macieandrz.securitycamera.viewModels.NotificationViewModel
import kotlinx.serialization.Serializable

@Serializable
object NotificationRoute


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    notificationViewModel: NotificationViewModel
) {
    var recipientEmail by remember { mutableStateOf("") }
    val currentEmail by notificationViewModel.recipientEmail.collectAsState()
    val context = LocalContext.current

    val notificationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.INTERNET)
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!notificationPermissionsState.allPermissionsGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Column {
                Button(
                    onClick = { notificationPermissionsState.launchMultiplePermissionRequest() },
                ) {
                    Text(text = "Ask for permissions")
                }
            }
        }
    }

    if (notificationPermissionsState.allPermissionsGranted || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {

        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Ustawienia powiadomień",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = recipientEmail,
                onValueChange = { recipientEmail = it },
                label = { Text("Adres email odbiorcy powiadomień") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                placeholder = { Text(currentEmail.ifEmpty { "Wprowadź adres email" }) }
            )

            Button(
                onClick = {
                    if (Patterns.EMAIL_ADDRESS.matcher(recipientEmail).matches()) {
                        notificationViewModel.saveRecipientEmail(recipientEmail)
                        Toast.makeText(context, "Email zapisany", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Nieprawidłowy adres email", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Zapisz")
            }

            if (currentEmail.isNotEmpty()) {
                Text(
                    text = "Bieżący adres email odbiorcy: $currentEmail",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}