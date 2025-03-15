package com.macieandrz.securitycamera.pages

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.macieandrz.securitycamera.ui.element.BottomNavigationBar
import com.macieandrz.securitycamera.viewModels.CrimeStatViewModel
import kotlinx.serialization.Serializable

@Serializable
object CrimeStatRoute

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CrimeStatPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    crimeStatViewModel: CrimeStatViewModel
) {

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                actualPosition = "CrimeStatPage"
            )
        }
    ) {paddingValues ->



    }


}