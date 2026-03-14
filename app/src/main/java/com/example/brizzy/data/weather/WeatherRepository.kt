package com.example.brizzy.data.weather

import com.example.brizzy.data.weather.model.AlertEntity
import com.example.brizzy.data.weather.model.FavoriteLocationEntity
import com.example.brizzy.data.weather.model.GeocodingResponseItem
import com.example.brizzy.data.weather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun getWeather(lat: Double, lon: Double): Flow<WeatherResponse>
    fun searchCity(cityName: String): Flow<List<GeocodingResponseItem>>
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocationEntity>>
    suspend fun insertFavoriteLocation(location: FavoriteLocationEntity)
    suspend fun deleteFavoriteLocation(location: FavoriteLocationEntity)
    fun getAllAlerts(): Flow<List<AlertEntity>>
    fun insertAlert(alert: AlertEntity)
    fun deleteAlert(alert: AlertEntity)
}