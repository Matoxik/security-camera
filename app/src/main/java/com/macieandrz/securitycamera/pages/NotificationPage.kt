package com.macieandrz.securitycamera.pages

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.macieandrz.securitycamera.viewModels.NotificationViewModel
import kotlinx.serialization.Serializable

@Serializable
object NotificationRoute

data class FriendEmail(
    val email: String,
    val uid: String = email // We use email as a unique identifier
)

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
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

    // State for permissions
    val notificationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.INTERNET)
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Checking permissions
        if (!notificationPermissionsState.allPermissionsGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Column {
                Button(
                    onClick = { notificationPermissionsState.launchMultiplePermissionRequest() },
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Ustawienia powiadomień",
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
                        label = { Text("Adres email przyjaciela") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        placeholder = { Text("Wprowadź adres email") }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                                notificationViewModel.addFriendEmail(FriendEmail(newEmail))
                                newEmail = "" // Clearing the field
                                Toast.makeText(context, "Email dodany", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Nieprawidłowy adres email", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Dodaj")
                    }
                }

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
}

@Composable
fun FriendEmailList(
    emails: List<FriendEmail>,
    onDelete: ((FriendEmail) -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (emails.isEmpty()) {
            Text(
                text = "Lista jest pusta",
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
    LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
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
                    contentDescription = "Usuń",
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
