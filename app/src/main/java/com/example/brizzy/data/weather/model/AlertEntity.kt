package com.example.brizzy.data.weather.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cityName: String,
    val lat: Double,
    val lon: Double,
    val startTime: Long,
    val endTime: Long,
    val isNotification: Boolean
)