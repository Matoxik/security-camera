package com.macieandrz.securitycamera.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val location by crimeStatViewModel.location.collectAsState(initial = null)


    var address by remember { mutableStateOf("") }


    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                actualPosition = "CrimeStatPage"
            )
        }
    ) {paddingValues ->


        Column(modifier = Modifier.padding(paddingValues)) {

            TextField(
                value = address,
                onValueChange = { address = it }
            )

            Button(onClick = {
                try {
                    crimeStatViewModel.performFetchSingleLocation(address)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }) {
                Text("Szukaj")
            }

            if (location != null) {
                Text(text = location.toString())

            }
        }



    }


}