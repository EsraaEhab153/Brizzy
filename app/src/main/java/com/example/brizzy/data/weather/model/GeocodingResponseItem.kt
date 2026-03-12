package com.example.brizzy.data.weather.model

import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponseItem(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String
)