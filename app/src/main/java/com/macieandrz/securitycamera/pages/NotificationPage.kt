package com.macieandrz.securitycamera.pages

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.macieandrz.securitycamera.ui.element.BottomNavigationBar
import com.macieandrz.securitycamera.viewModels.NotificationViewModel
import kotlinx.serialization.Serializable

@Serializable
object NotificationRoute

data class FriendEmail(
    val email: String,
    val uid: String = email // email as a unique identifier
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    notificationViewModel: NotificationViewModel
) {
    val context = LocalContext.current
    // State for the new email
    var newEmail by remember { mutableStateOf("") }
    // State for the friend emails list
    val friendEmails by notificationViewModel.friendEmails.collectAsState()
    val isMotionDetectionEnabled by notificationViewModel.isMotionDetectionEnabled.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // State for permissions
    val notificationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.INTERNET)
    )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                actualPosition = "NotificationPage"
            )
        }
    ) { paddingValues ->

        // Checking permissions
            if (!notificationPermissionsState.allPermissionsGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Column {
                    Button(
                        onClick = { notificationPermissionsState.launchMultiplePermissionRequest() },
                        shape = CutCornerShape(8.dp)
                    ) {
                        Text(text = "Ask for permissions")
                    }
                }
            } else {
                // Landscape orientation
                if (isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Email input
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newEmail,
                                    onValueChange = { newEmail = it },
                                    label = { Text("Send notification to") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    placeholder = { Text("Enter email address") }
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Button(
                                    shape = CutCornerShape(8.dp),
                                    onClick = {
                                        if (Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                                            notificationViewModel.addFriendEmail(
                                                FriendEmail(newEmail)
                                            )
                                            newEmail = ""
                                            Toast.makeText(
                                                context,
                                                "Email added correctly",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Invalid email address",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                ) {
                                    Text("Add")
                                }
                            }

                            // Motion detection switch
                            MotionDetectionSwitch(
                                isMotionDetectionEnabled = isMotionDetectionEnabled,
                                notificationViewModel = notificationViewModel
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Email list",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            FriendEmailList(
                                emails = friendEmails,
                                onDelete = { email ->
                                    notificationViewModel.removeFriendEmail(email)
                                }
                            )
                        }
                    }
                } else {
                    // Portrait orientation
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Checking permissions
                            if (!notificationPermissionsState.allPermissionsGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Column {
                                    Button(
                                        onClick = { notificationPermissionsState.launchMultiplePermissionRequest() },
                                        shape = CutCornerShape(8.dp)
                                    ) {
                                        Text(text = "Ask for permissions")
                                    }
                                }
                            } else {
                                // Main content
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Notification settings",
                                        style = MaterialTheme.typography.headlineMedium,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )

                                    // Email input row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = newEmail,
                                            onValueChange = { newEmail = it },
                                            label = { Text("Send notification to") },
                                            modifier = Modifier.weight(1f),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                            placeholder = { Text("Enter email address") }
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Button(
                                            shape = CutCornerShape(8.dp),
                                            onClick = {
                                                if (Patterns.EMAIL_ADDRESS.matcher(newEmail)
                                                        .matches()
                                                ) {
                                                    notificationViewModel.addFriendEmail(
                                                        FriendEmail(
                                                            newEmail
                                                        )
                                                    )
                                                    newEmail = "" // Clearing the field
                                                    Toast.makeText(
                                                        context,
                                                        "Email added correctly",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Invalid email address",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        ) {
                                            Text("Add")
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))

                                    // List of friend emails
                                    FriendEmailList(
                                        emails = friendEmails,
                                        onDelete = { email ->
                                            notificationViewModel.removeFriendEmail(email)
                                        }
                                    )


                                }
                            }
                        }

                        // Motion detection
                        Spacer(modifier = Modifier.height(12.dp))
                        MotionDetectionSwitch(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                            isMotionDetectionEnabled,
                            notificationViewModel
                        )
                    }
                }
            }
        }
}


// Motion detection
@Composable
fun MotionDetectionSwitch(
    modifier: Modifier = Modifier,
    isMotionDetectionEnabled: Boolean,
    notificationViewModel: NotificationViewModel
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Camera motion detection",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Notify when phone position changes",
                    style = MaterialTheme.typography.bodyMedium
                )

                Switch(
                    checked = isMotionDetectionEnabled,
                    onCheckedChange = { enabled ->
                        notificationViewModel.setMotionDetectionEnabled(enabled)
                    }
                )
            }
        }
    }
}

@Composable
fun FriendEmailList(
    emails: List<FriendEmail>,
    onDelete: ((FriendEmail) -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (emails.isEmpty()) {
            Text(
                text = "List is empty",
                modifier = Modifier.padding(top = 32.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            FriendEmailLazyColumn(emails, onDelete)
        }
    }
}

@Composable
fun FriendEmailLazyColumn(
    emails: List<FriendEmail>,
    onDelete: ((FriendEmail) -> Unit)? = null
) {
    LazyColumn(modifier = Modifier.padding(top = 16.dp, bottom = 40.dp)) {
        items(items = emails, key = { it.uid }) { email ->
            FriendEmailRow(email, onDelete)
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun FriendEmailRow(
    email: FriendEmail,
    onDelete: ((FriendEmail) -> Unit)? = null
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart)
                onDelete?.invoke(email)
            true
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 100.dp, end = 10.dp, top = 2.dp, bottom = 2.dp)
                    .background(Color.Red),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        },
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = false
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(10.dp),
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = email.email,
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Normal
                )
            }
        }
    }
}

