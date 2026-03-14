package com.example.brizzy.data.weather.datasource.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.brizzy.data.weather.model.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteLocationDao {

    @Query("SELECT * FROM favorite_locations")
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertFavoriteLocation(location: FavoriteLocationEntity)

    @Delete
     fun deleteFavoriteLocation(location: FavoriteLocationEntity)
}