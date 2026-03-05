package com.example.brizzy.presentation.home.viewModel

import com.example.brizzy.data.weather.model.WeatherResponse

sealed class WeatherUiState {
    data object Loading : WeatherUiState()
    data class Success(val weather: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}