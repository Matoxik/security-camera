package com.macieandrz.securitycamera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.macieandrz.securitycamera.pages.HomePage
import com.macieandrz.securitycamera.pages.HomeRoute
import com.macieandrz.securitycamera.pages.LoginPage
import com.macieandrz.securitycamera.pages.LoginRoute
import com.macieandrz.securitycamera.pages.SignupPage
import com.macieandrz.securitycamera.pages.SignupRoute

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
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
         }
}