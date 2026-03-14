package com.example.brizzy.presentation.favorite.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.brizzy.presentation.favorite.viewModel.FavoritesViewModel
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.brizzy.R
import com.example.brizzy.data.weather.model.FavoriteLocationEntity
import com.example.brizzy.data.weather.model.WeatherResponse
import com.example.brizzy.utils.getWeatherBackgroundColors
import com.example.brizzy.utils.getWeatherLottieAnim

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onAddLocationClick: () -> Unit,
    onLocationClick: (lat: Double, lon: Double) -> Unit
) {
    val favoriteLocations by viewModel.favoriteLocations.collectAsState()
    val showConfirmDialog by viewModel.showConfirmDialog
    val locationToDelete by viewModel.locationToDelete
    val weatherMap by viewModel.weatherDataMap.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F1B3C))) {
        Column(modifier = Modifier.fillMaxSize()) {

            FavoritesHeader(
                onAddLocationClick = onAddLocationClick
            )
            if(favoriteLocations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                       Image(
                           painter = painterResource(id = R.drawable.no_favorites),
                           contentDescription = "no favorite places",
                       )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No favorite locations yet.",
                            color = Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Click '+' to add some!",
                            color = Color.White.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            else{
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(
                        items = favoriteLocations,
                        key = { location -> location.id }
                    ) { location ->
                        val weather = weatherMap[location.id]
                        SwipeToDeleteBox(
                            location = location,
                            weather = weather,
                            onDeleteInitiated = { viewModel.requestDelete(location) },
                            onClick = { onLocationClick(location.latitude, location.longitude) }
                        )
                    }
                }
            }

        }

        DeleteConfirmationDialog(
            show = showConfirmDialog,
            locationName = locationToDelete?.cityName ?: "",
            onConfirm = { viewModel.confirmDelete() },
            onDismiss = { viewModel.cancelDelete() }
        )
    }
}

@Composable
fun FavoritesHeader(
    onAddLocationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 64.dp, end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Favorites", style = MaterialTheme.typography.headlineLarge, color = Color.White)
            Text("Saved locations", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(top = 4.dp))
        }
        FloatingActionButton(
            onClick = onAddLocationClick,
            containerColor = Color(0x6829B2DD),
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add new location")
        }
    }
}
@Composable
fun LocationCard(
    location: FavoriteLocationEntity,
    weather: WeatherResponse?,
    onClick: () -> Unit
) {
    val iconCode = weather?.list?.get(0)?.weather?.get(0)?.icon ?: "01d"
    val targetColors = getWeatherBackgroundColors(iconCode)

    val topColor by animateColorAsState(
        targetValue = targetColors[0],
        animationSpec = tween(durationMillis = 1000),
        label = "Top Color Animation"
    )
    val bottomColor by animateColorAsState(
        targetValue = targetColors[1],
        animationSpec = tween(durationMillis = 1000),
        label = "Bottom Color Animation"
    )

    val animatedGradient = Brush.verticalGradient(listOf(topColor, bottomColor))

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).height(100.dp).background(brush = animatedGradient, shape = RoundedCornerShape(24.dp)).clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(location.cityName, color = Color.White, modifier = Modifier.weight(1f))
            if (weather != null) {
                val temp = weather.list?.get(0)?.main?.temp?.toInt() ?: 0
                val iconString = weather.list?.get(0)?.weather?.get(0)?.icon ?: "01d"

                val animResId = getWeatherLottieAnim(iconString)

                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animResId))
                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )

                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(70.dp).padding(end = 16.dp),
                    contentScale = ContentScale.Fit
                )
                Text("$temp°", color = Color.White, style = MaterialTheme.typography.headlineLarge)
            } else {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteBox(
    location: FavoriteLocationEntity,
    weather: WeatherResponse?,
    onDeleteInitiated: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDeleteInitiated()
                return@rememberSwipeToDismissBoxState false
            }
            return@rememberSwipeToDismissBoxState false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled)
                    Color.Transparent
                else
                    Color.Red.copy(alpha = 0.8f),
                label = "color_animation"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(color)
            ) {
                if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 24.dp)
                    )
                }
            }
        },
        content = {
            LocationCard(
                location = location,
                weather = weather,
                onClick = onClick
            )
        },
        enableDismissFromStartToEnd = false
    )
}

@Composable
fun DeleteConfirmationDialog(
    show: Boolean,
    locationName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete '$locationName' from your favorites?") },
            confirmButton = {
                TextButton(onClick = onConfirm, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF29B2DD))) {
                    Text("Undo / Cancel")
                }
            },
            containerColor = Color(0xFF0F1B3C),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.7f),
            shape = RoundedCornerShape(24.dp)
        )
    }
}