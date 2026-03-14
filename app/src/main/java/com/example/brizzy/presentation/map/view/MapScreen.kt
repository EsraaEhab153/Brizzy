package com.example.brizzy.presentation.map.view

import android.content.Context
import android.location.Geocoder
import android.preference.PreferenceManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.brizzy.presentation.map.viewModel.MapViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onLocationSelected: (cityName: String,lat: Double, lon: Double) -> Unit
) {
    val context = LocalContext.current
    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isDropdownExpanded = searchResults.isNotEmpty()

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    mapView = this
                    setMultiTouchControls(true)
                    controller.setZoom(7.0)
                    controller.setCenter(GeoPoint(30.0444, 31.2357))

                    val mapEventsReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                            selectedPoint = p
                            overlays.removeAll { it is Marker }
                            val marker = Marker(this@apply).apply {
                                position = p
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            overlays.add(marker)
                            invalidate()

                            viewModel.onSearchQueryChanged("", isSelectingFromList = true)
                            return true
                        }
                        override fun longPressHelper(p: GeoPoint): Boolean = false
                    }
                    overlays.add(MapEventsOverlay(mapEventsReceiver))
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Search for a city...", color = Color.White, textAlign = TextAlign.Start) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF0F2B6B),
                    unfocusedContainerColor = Color(0xFF0F2B6B),
                    focusedIndicatorColor = Color(0xFF29B2DD),
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xFF29B2DD),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )

            if (isDropdownExpanded) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2B6B))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        searchResults.forEach { city ->
                            Text(
                                text = "${city.name}, ${city.country}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onSearchQueryChanged(city.name, isSelectingFromList = true)
                                        val newPoint = GeoPoint(city.lat, city.lon)
                                        selectedPoint = newPoint

                                        mapView?.controller?.animateTo(newPoint, 10.0, 1500L)
                                        mapView?.overlays?.removeAll { it is Marker }
                                        val marker = Marker(mapView).apply {
                                            position = newPoint
                                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        }
                                        mapView?.overlays?.add(marker)
                                        mapView?.invalidate()
                                    }
                                    .padding(16.dp),
                                color = Color.White
                            )
                            Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
        val scope = rememberCoroutineScope()
        if (selectedPoint != null) {
            Button(
                onClick = {
                    scope.launch {
                        val finalCityName = if (searchQuery.isNotBlank()) {
                            searchQuery
                        } else {
                             getCityNameFromCoordinates(context, selectedPoint!!.latitude, selectedPoint!!.longitude)
                        }
                        onLocationSelected(finalCityName, selectedPoint!!.latitude, selectedPoint!!.longitude)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29B2DD)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp, start = 24.dp, end = 24.dp)
                    .fillMaxWidth()
            ) {
                Text("Confirm Location", color = Color.White)
            }
        }
    }
}

suspend fun getCityNameFromCoordinates(context: Context, lat: Double, lon: Double): String {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                address.locality ?: address.subAdminArea ?: address.adminArea ?: address.countryName ?: "Unknown Location"
            } else {
                "Unknown Location"
            }
        } catch (e: Exception) {
            "Pinned Location"
        }
    }
}