package com.example.brizzy.data.weather.datasource.local.room

import com.example.brizzy.data.weather.model.AlertEntity
import com.example.brizzy.data.weather.model.FavoriteLocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeatherLocalDataSource(
    private val favoriteLocationDao: FavoriteLocationDao,
    private val alertDao: AlertDao
): IWeatherLocalDataSource {

    override suspend fun insertFavoriteLocation(location: FavoriteLocationEntity) {
        withContext(Dispatchers.IO) {
            favoriteLocationDao.insertFavoriteLocation(location)
        }
    }

    override suspend fun deleteFavoriteLocation(location: FavoriteLocationEntity) {
        withContext(Dispatchers.IO) {
            favoriteLocationDao.deleteFavoriteLocation(location)
        }
    }

    override fun getAllFavoriteLocations(): Flow<List<FavoriteLocationEntity>> {
        return favoriteLocationDao.getAllFavoriteLocations()
    }

    override fun insertAlert(alert: AlertEntity) {
        alertDao.insertAlert(alert)
    }

    override fun deleteAlert(alert: AlertEntity) {
        alertDao.deleteAlert(alert)
    }

    override fun getAllAlerts(): Flow<List<AlertEntity>> {
        return alertDao.getAllAlerts()
    }
}