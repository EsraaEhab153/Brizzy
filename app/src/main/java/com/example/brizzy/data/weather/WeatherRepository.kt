package com.example.brizzy.data.weather

import com.example.brizzy.data.weather.model.GeocodingResponseItem
import com.example.brizzy.data.weather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun getWeather(lat: Double, lon: Double): Flow<WeatherResponse>
    fun searchCity(cityName: String): Flow<List<GeocodingResponseItem>>
}