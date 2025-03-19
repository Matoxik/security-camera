package com.macieandrz.securitycamera.pages

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.macieandrz.securitycamera.ui.element.BottomNavigationBar
import com.macieandrz.securitycamera.ui.element.CrimeStatChart
import com.macieandrz.securitycamera.viewModels.CrimeStatViewModel
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
object CrimeStatRoute

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CrimeStatPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    crimeStatViewModel: CrimeStatViewModel
) {
    // State variables for UI
    val location by crimeStatViewModel.location.collectAsState(initial = null)
    val crimeStats by crimeStatViewModel.crimeStat.collectAsState(initial = null)
    var address by remember { mutableStateOf("") }

    // Date handling
    val currentDate = remember { Calendar.getInstance() }
    val currentDateMillis = remember { currentDate.timeInMillis }

    // Check if the device is in landscape orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Date picker state setup
    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = currentDateMillis,
        selectableDates = object : SelectableDates {
            // Ensure only past and present dates are selectable
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= currentDateMillis
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year <= currentDate.get(Calendar.YEAR)
            }
        }
    )

    var showDateDialog by remember { mutableStateOf(false) }

    // Initialize date with current date in YYYY-MM format
    val initialDate = remember {
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH) - 1
        "${year}-${month.toString().padStart(2, '0')}"
    }

    var date by remember { mutableStateOf(initialDate) }

    // Function to update crime statistics
    val updateCrimeStats = {
        if (location != null) {
            crimeStatViewModel.performFetchSingleCrimeStat(
                date,
                location!!.lat,
                location!!.lng
            )
        }
    }

    // Render different layouts based on orientation
    if (isLandscape) {
        // Landscape layout
        crimeStats?.let {
            if (it.isNotEmpty()) {
                CrimeStatChart(crimeStats = it)
            }
        }
    } else {
        // Portrait layout
        Scaffold(
            modifier = modifier,
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    actualPosition = "CrimeStatPage"
                )
            },
            floatingActionButton = {
                // FAB to clear databases
                SmallFloatingActionButton(
                    onClick = {
                        crimeStatViewModel.clearDatabases()
                        address = ""
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear databases"
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { paddingValues ->
            Column(modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)) {
                // Date selection UI
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)) {
                    TextField(
                        value = date,
                        onValueChange = { /* Read-only field */ },
                        label = { Text("Date (YYYY-MM)") },
                        modifier = Modifier.weight(1f),
                        readOnly = true
                    )

                    Button(
                        onClick = { showDateDialog = true },
                        modifier = Modifier.padding(start = 8.dp),
                        shape = CutCornerShape(8.dp)
                    ) {
                        Text("Select")
                    }
                }

                // Location input field
                TextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Location") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Search button
                Button(
                    onClick = {
                        if (address.isNotBlank()) {
                            crimeStatViewModel.performFetchSingleLocation(address)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CutCornerShape(8.dp)
                ) {
                    Text("Search")
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Display chart or message based on available data
                crimeStats?.let {
                    if (it.isNotEmpty()) {
                        CrimeStatChart(crimeStats = it)
                    } else {
                        Text(
                            text = "No crime data available for this location and date",
                            modifier = Modifier.padding(vertical = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Date Picker Dialog
                if (showDateDialog) {
                    DatePickerDialog(
                        onDismissRequest = { showDateDialog = false },
                        confirmButton = {
                            Button(
                                onClick = {
                                    dateState.selectedDateMillis?.let { millis ->
                                        val calendar = Calendar.getInstance().apply {
                                            timeInMillis = millis
                                        }

                                        val year = calendar.get(Calendar.YEAR)
                                        val month = calendar.get(Calendar.MONTH) + 1
                                        val newDate = "${year}-${month.toString().padStart(2, '0')}"

                                        // Update date and fetch new statistics if date changed
                                        if (newDate != date) {
                                            date = newDate
                                            updateCrimeStats()
                                        }
                                    }
                                    showDateDialog = false
                                },
                                shape = CutCornerShape(8.dp)
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showDateDialog = false },
                                shape = CutCornerShape(8.dp)
                            ) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = dateState)
                    }
                }
            }
        }

        // Effect to automatically fetch statistics when location changes
        LaunchedEffect(location) {
            if (location != null) {
                updateCrimeStats()
            }
        }
    }
}
