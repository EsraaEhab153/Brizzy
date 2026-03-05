package com.example.brizzy.presentation.home.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brizzy.presentation.home.viewModel.HomeViewModel
import com.example.brizzy.utils.UiState

@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getWeatherData(lat = 30.7865, lon = 31.0004)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
            is UiState.Error -> {
                val errorMessage = (uiState as UiState.Error).message
                Text(text = "Error: $errorMessage", color = Color.Red)
            }
            is UiState.Success -> {
                val weatherData = (uiState as UiState.Success).data

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = weatherData.city.name, fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${weatherData.list[0].main.temp} °C",
                        fontSize = 48.sp,
                        color = Color.Blue
                    )
                    Text(text = weatherData.list[0].weather[0].description)
                }
            }
        }
    }
}