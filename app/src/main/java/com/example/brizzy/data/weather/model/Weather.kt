package com.example.brizzy.data.weather.model

data class WeatherModel(
    val cityName: String,
    val dateText: String,
    val temperature: Double,
    val description: String,
    val icon: String,
    val windSpeed: Double,
    val humidity: Int
)