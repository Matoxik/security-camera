package com.macieandrz.securitycamera.pages

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.macieandrz.securitycamera.R
import com.macieandrz.securitycamera.ui.theme.Orange80
import com.macieandrz.securitycamera.ui.theme.OrangeGrey80
import com.macieandrz.securitycamera.ui.theme.Yellow80
import com.macieandrz.securitycamera.ui.theme.Orange40
import com.macieandrz.securitycamera.viewModels.AuthState
import com.macieandrz.securitycamera.viewModels.AuthViewModel
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState = authViewModel.authState.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE


    Scaffold { innerPadding ->
        ConstraintLayout(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            val (appLogo, form) = createRefs()

            LaunchedEffect(authState.value) {
                when (authState.value) {
                    is AuthState.UnAuthenticated -> navController.navigate(LoginRoute)
                    else -> Unit
                }
            }

            // Horizontal layout - delete logo image
            if (!isLandscape) {
                Column(
                    modifier = Modifier
                        .constrainAs(appLogo) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.size(24.dp))

                    Image(
                        modifier = Modifier.size(140.dp),
                        painter = painterResource(id = R.drawable.logo_round),
                        contentDescription = "App logo"
                    )

                    Spacer(Modifier.size(24.dp))
                }
            }


            LazyColumn(
                modifier = modifier
                    .constrainAs(form) {
                        top.linkTo(appLogo.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(Modifier.size(24.dp))
                    // Turn on security camera button
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate(CameraRoute) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Camera,
                                    contentDescription = "Camera Icon",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Turn on security camera",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gallery button
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate(GalleryRoute) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PhotoLibrary,
                                    contentDescription = "Gallery Icon",
                                    tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Gallery",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Notification Settings button
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate(NotificationRoute) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Notifications,
                                    contentDescription = "Notifications Icon",
                                    tint = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Notification settings",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Logout button
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { authViewModel.signout() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.background),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = "Logout Icon",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Logout",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}