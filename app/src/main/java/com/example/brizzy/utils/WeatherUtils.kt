package com.example.brizzy.utils

import androidx.annotation.RawRes
import androidx.compose.ui.graphics.Color
import com.example.brizzy.R

@RawRes
fun getWeatherLottieAnim(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> R.raw.sunny
        "01n" -> R.raw.weather_night


        "02d" -> R.raw.cloudy_day
        "02n" -> R.raw.weather_cloudy_night

        "03d", "03n",
        "04d", "04n" -> R.raw.weather_windy


        "09d", "09n" -> R.raw.rain_icon

        "10d" -> R.raw.weather_partly_shower
        "10n" -> R.raw.weather_rainy_night

        "11d", "11n" -> R.raw.weather_storm

        "13d", "13n" -> R.raw.snowing

        "50d", "50n" -> R.raw.mist

        else -> R.raw.weather_windy
    }
}

fun getWeatherBackgroundColors(iconCode: String?): List<Color> {
    return when (iconCode) {
        "01d" -> listOf(Color(0xFF29B2DD), Color(0xFF33AADD))

        "01n" -> listOf(Color(0xFF08244F), Color(0xFF134CB5))

        "02d", "03d", "04d" -> listOf(Color(0xFF5983A8), Color(0xFF86A8C9))

        "02n", "03n", "04n" -> listOf(Color(0xFF1E2836), Color(0xFF304052))

        "09d", "10d", "11d", "50d" -> listOf(Color(0xFF4A5E6D), Color(0xFF677E90))

        "09n", "10n", "11n", "50n" -> listOf(Color(0xFF141E30), Color(0xFF243B55))

        "13d", "13n" -> listOf(Color(0xFF6A93CB), Color(0xFFA4BFE3))

        else -> listOf(Color(0xFF2B70E4), Color(0xFF0F2B6B))
    }
}