package com.macieandrz.securitycamera.pages

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.macieandrz.securitycamera.ui.element.BottomNavigationBar
import com.macieandrz.securitycamera.viewModels.GalleryViewModel
import kotlinx.serialization.Serializable

// Serializable object for navigation routing
@Serializable
object GalleryRoute

@Composable
fun GalleryPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    galleryViewModel: GalleryViewModel
) {
    // Collect user images from the ViewModel
    val userImages by galleryViewModel.userImages.collectAsState()

    // State to track the selected image URL and whether it's expanded
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    var isExpanded by remember { mutableStateOf(false) }

    // Scaffold to structure the page with a bottom navigation bar
    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                actualPosition = "GalleryPage"
            )
        }
    ) { paddingValues ->
        // LazyVerticalGrid to display images in a 2-column grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(8.dp),
        ) {
            // Iterate over the reversed list of user images
            items(userImages.reversed()) { imageUrl ->
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .heightIn(min = 200.dp)
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        // Set the selected image URL and expand it
                        selectedImageUrl = imageUrl
                        isExpanded = true
                    }
                ) {
                    // Display the image using AsyncImage
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // Transition animation for scaling and fading the expanded image
        val transition = updateTransition(targetState = isExpanded, label = "ImageTransition")
        val scale by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 300) },
            label = "Scale"
        ) { expanded ->
            if (expanded) 1f else 0.5f // Scale up if expanded, down otherwise
        }
        val cardAlpha by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 300) },
            label = "CardAlpha"
        ) { expanded ->
            if (expanded) 1f else 0f // Fade in if expanded, out otherwise
        }

        // Display the enlarged photo view if an image is selected
        if (selectedImageUrl != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)) // Semi-transparent background
                    .clickable {
                        // Collapse the image when clicked
                        isExpanded = false
                        selectedImageUrl = null
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = selectedImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            alpha = cardAlpha
                        }
                        .fillMaxSize(0.95f) // Slightly smaller than full size
                        .animateContentSize(), // Animate size changes
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
