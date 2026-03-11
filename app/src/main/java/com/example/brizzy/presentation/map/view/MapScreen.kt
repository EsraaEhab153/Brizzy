package com.example.brizzy.presentation.map.view

import android.preference.PreferenceManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(
    onLocationSelected: (lat: Double, lon: Double) -> Unit
) {
    val context = LocalContext.current
    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
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
                                title = "Selected Location"
                            }
                            overlays.add(marker)
                            invalidate()
                            return true
                        }

                        override fun longPressHelper(p: GeoPoint): Boolean = false
                    }

                    overlays.add(MapEventsOverlay(mapEventsReceiver))
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (selectedPoint != null) {
            Button(
                onClick = {
                    onLocationSelected(selectedPoint!!.latitude, selectedPoint!!.longitude)
                    Log.d("Map", "MapScreen: ${selectedPoint!!.latitude} ${selectedPoint!!.longitude}")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29B2DD)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp, start = 24.dp, end = 24.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Confirm Location", color = Color.White)
            }
        }
    }
}