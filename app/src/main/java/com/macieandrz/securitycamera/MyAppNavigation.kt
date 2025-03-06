package com.macieandrz.securitycamera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.macieandrz.securitycamera.pages.CameraPage
import com.macieandrz.securitycamera.pages.CameraRoute
import com.macieandrz.securitycamera.pages.GalleryPage
import com.macieandrz.securitycamera.pages.GalleryRoute
import com.macieandrz.securitycamera.pages.HomePage
import com.macieandrz.securitycamera.pages.HomeRoute
import com.macieandrz.securitycamera.pages.LoginPage
import com.macieandrz.securitycamera.pages.LoginRoute
import com.macieandrz.securitycamera.pages.ResetPassPage
import com.macieandrz.securitycamera.pages.ResetPassRoute
import com.macieandrz.securitycamera.pages.SignupPage
import com.macieandrz.securitycamera.pages.SignupRoute
import com.macieandrz.securitycamera.viewModels.AuthViewModel
import com.macieandrz.securitycamera.viewModels.CameraViewModel
import com.macieandrz.securitycamera.viewModels.GalleryViewModel

@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    cameraViewModel: CameraViewModel,
    galleryViewModel: GalleryViewModel
    ) {
    val navController = rememberNavController()

    NavHost(
        navController = navController, startDestination = LoginRoute
    ) {
        composable<LoginRoute> {
            LoginPage(modifier, navController, authViewModel)
        }
        composable<SignupRoute> {
            SignupPage(modifier, navController, authViewModel)
        }
        composable<HomeRoute> {
            HomePage(modifier, navController, authViewModel)
        }
        composable<ResetPassRoute> {
            ResetPassPage(modifier, navController, authViewModel)
        }
        composable<CameraRoute> {
            CameraPage(modifier, navController, cameraViewModel)
        }
        composable<GalleryRoute> {
            GalleryPage(modifier, navController, galleryViewModel)
        }

    }
}