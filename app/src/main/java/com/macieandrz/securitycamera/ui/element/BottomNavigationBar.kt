package com.macieandrz.securitycamera.ui.element

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.InsertChart
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.macieandrz.securitycamera.pages.CameraRoute
import com.macieandrz.securitycamera.pages.CrimeStatRoute
import com.macieandrz.securitycamera.pages.GalleryRoute
import com.macieandrz.securitycamera.pages.HomeRoute
import com.macieandrz.securitycamera.pages.NotificationRoute
import com.macieandrz.securitycamera.ui.theme.cameraBackground
import com.macieandrz.securitycamera.ui.theme.cameraTint
import com.macieandrz.securitycamera.ui.theme.crimeStatBackground
import com.macieandrz.securitycamera.ui.theme.crimeStatTint
import com.macieandrz.securitycamera.ui.theme.galleryBackground
import com.macieandrz.securitycamera.ui.theme.galleryTint
import com.macieandrz.securitycamera.ui.theme.homeBackground
import com.macieandrz.securitycamera.ui.theme.homeTint
import com.macieandrz.securitycamera.ui.theme.notificationBackground
import com.macieandrz.securitycamera.ui.theme.notificationTint


@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    actualPosition: String
) {
                NavigationBar(modifier = modifier,
                    windowInsets = WindowInsets(0, 0, 0, 0)
                    ) {
                        // Camera
                        NavigationBarItem(
                            selected = actualPosition == "CameraPage",
                            onClick = {
                                navController.navigate(CameraRoute)
                            },
                            label = {
                                Text("Camera")
                            },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(cameraBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (actualPosition == "CameraPage") {
                                            Icons.Filled.Camera
                                        } else Icons.Outlined.Camera,
                                        contentDescription = "Camera Icon",
                                        tint = cameraTint
                                    )
                                }
                            }
                        )


                        // Gallery
                        NavigationBarItem(
                            selected = actualPosition == "GalleryPage",
                            onClick = {
                                navController.navigate(GalleryRoute)
                            },
                            label = {
                                Text("Gallery")
                            },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(galleryBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (actualPosition == "GalleryPage") {
                                            Icons.Filled.PhotoLibrary
                                        } else Icons.Outlined.PhotoLibrary,
                                        contentDescription = "Gallery Icon",
                                        tint = galleryTint
                                    )
                                }
                            }
                        )


                        // Crime statistics
                        NavigationBarItem(
                            selected = actualPosition == "CrimeStatPage",
                            onClick = {
                                navController.navigate(CrimeStatRoute)
                            },
                            label = {
                                Text("Statistics")
                            },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(crimeStatBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (actualPosition == "CrimeStatPage") {
                                            Icons.Filled.InsertChart
                                        } else Icons.Outlined.InsertChart,
                                        contentDescription = "Crime stats Icon",
                                        tint = crimeStatTint
                                    )
                                }
                            }
                        )


                        // Notification settings
                        NavigationBarItem(
                            selected = actualPosition == "NotificationPage",
                            onClick = {
                                navController.navigate(NotificationRoute)
                            },
                            label = {
                                Text("Notice")
                            },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(notificationBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (actualPosition == "NotificationPage"){
                                            Icons.Filled.Notifications
                                        } else Icons.Outlined.Notifications,
                                        contentDescription = "Notifications Icon",
                                        tint = notificationTint
                                    )
                                }
                            }
                        )


                    // Home
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            navController.navigate(HomeRoute)
                        },
                        label = {
                            Text("Home")
                        },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(homeBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Home,
                                    contentDescription = "Home Icon",
                                    tint = homeTint
                                )
                            }
                        }
                    )

                }
            }


