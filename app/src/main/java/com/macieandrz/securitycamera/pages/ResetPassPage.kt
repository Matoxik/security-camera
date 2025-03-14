package com.macieandrz.securitycamera.pages

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.macieandrz.securitycamera.R
import com.macieandrz.securitycamera.viewModels.AuthState
import com.macieandrz.securitycamera.viewModels.AuthViewModel
import kotlinx.serialization.Serializable

@Serializable
object ResetPassRoute

@Composable
fun ResetPassPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by rememberSaveable { mutableStateOf("") }
    val authState = authViewModel.authState.collectAsState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
    ) { innerPadding ->

        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            val (appLogo, form) = createRefs()



            LaunchedEffect(authState.value) {
                when (authState.value) {
                    is AuthState.Authenticated -> navController.navigate(HomeRoute)
                    is AuthState.Error -> Toast.makeText(
                        context,
                        (authState.value as AuthState.Error).message,
                        Toast.LENGTH_SHORT
                    ).show()

                    is AuthState.PasswordReseted -> Toast.makeText(
                        context,
                        "Email sent successfully to reset your password!",
                        Toast.LENGTH_SHORT
                    ).show()

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

                Column(
                    modifier = Modifier
                        .constrainAs(form) {
                            top.linkTo(appLogo.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .fillMaxSize()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,

                    ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "FORGOT PASSWORD?",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = "Enter your e-mail address and\nwe'll send you an email to reset your password",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                        },
                        label = {
                            Text(text = "Email")
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        shape = CutCornerShape(8.dp),
                        onClick = {
                            authViewModel.resetPassword(email)
                            navController.navigate(LoginRoute)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Submit")
                    }
                }
            }

        }
    }
