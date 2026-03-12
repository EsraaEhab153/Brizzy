package com.example.brizzy.data.weather.datasource.local.room

import com.example.brizzy.data.weather.model.FavoriteLocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeatherLocalDataSource(private val favoriteLocationDao: FavoriteLocationDao) {

    suspend fun insertFavoriteLocation(location: FavoriteLocationEntity) {
        withContext(Dispatchers.IO) {
            favoriteLocationDao.insertFavoriteLocation(location)
        }
    }

    suspend fun deleteFavoriteLocation(location: FavoriteLocationEntity) {
        withContext(Dispatchers.IO) {
            favoriteLocationDao.deleteFavoriteLocation(location)
        }
    }

    fun getAllFavoriteLocations(): Flow<List<FavoriteLocationEntity>> {
        return favoriteLocationDao.getAllFavoriteLocations()
    }
}