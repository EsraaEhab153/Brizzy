package com.example.brizzy.data.weather.datasource.local.room

import com.example.brizzy.data.weather.model.AlertEntity
import com.example.brizzy.data.weather.model.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDataSource {
    fun getAllAlerts(): Flow<List<AlertEntity>>
    fun insertAlert(alert: AlertEntity)
    fun deleteAlert(alert: AlertEntity)
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocationEntity>>
    suspend fun insertFavoriteLocation(location: FavoriteLocationEntity)
    suspend fun deleteFavoriteLocation(location: FavoriteLocationEntity)
}