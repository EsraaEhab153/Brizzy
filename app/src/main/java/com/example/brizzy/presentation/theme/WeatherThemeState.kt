package com.example.brizzy.presentation.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object WeatherThemeState {
    var currentColors by mutableStateOf(
        listOf(Color(0xFF2B70E4), Color(0xFF0F2B6B))
    )
}