package com.example.brizzy.data.weather

import com.example.brizzy.data.weather.datasource.local.room.IWeatherLocalDataSource
import com.example.brizzy.data.weather.model.AlertEntity
import com.example.brizzy.data.weather.model.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeWeatherLocalDataSource(
    var alerts: MutableList<AlertEntity> = mutableListOf(),
    var favoritesList: MutableList<FavoriteLocationEntity> = mutableListOf()
) : IWeatherLocalDataSource {

    override fun getAllAlerts(): Flow<List<AlertEntity>> {
        return flowOf(alerts)
    }

    override  fun insertAlert(alert: AlertEntity) {
        alerts.add(alert)
    }

    override  fun deleteAlert(alert: AlertEntity) {
        alerts.remove(alert)
    }

    override fun getAllFavoriteLocations(): Flow<List<FavoriteLocationEntity>> {
        return kotlinx.coroutines.flow.flowOf(favoritesList)
    }

    override suspend fun insertFavoriteLocation(location: FavoriteLocationEntity) {
        favoritesList.add(location)
    }

    override suspend fun deleteFavoriteLocation(location: FavoriteLocationEntity) {
        favoritesList.remove(location)
    }

}