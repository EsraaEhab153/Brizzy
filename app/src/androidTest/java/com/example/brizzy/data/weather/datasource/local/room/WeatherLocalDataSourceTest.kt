package com.example.brizzy.data.weather.datasource.local.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.brizzy.data.db.WeatherDatabase
import com.example.brizzy.data.weather.datasource.local.room.WeatherLocalDataSource
import com.example.brizzy.data.weather.model.AlertEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherLocalDataSourceTest {

    private lateinit var database: WeatherDatabase
    private lateinit var localDataSource: WeatherLocalDataSource

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()

        localDataSource = WeatherLocalDataSource(database.favoriteLocationDao(), database.alertDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAlert_retrievesAlert() = runTest {
        // Given
        val alert = AlertEntity(id = 1, cityName = "Cairo", lat = 30.0, lon = 31.0, startTime = 0L, endTime = 0L, isNotification = true)

        // When
        localDataSource.insertAlert(alert)
        val result = localDataSource.getAllAlerts().first()

        // Then
        assertThat(result.size,`is`(1))
        assertThat(result[0].cityName,`is`("Cairo"))
    }

    @Test
    fun deleteAlert_removesAlert() = runTest {
        // Given
        val alert = AlertEntity(id = 2, cityName = "Alex", lat = 31.0, lon = 29.0, startTime = 0L, endTime = 0L, isNotification = false)
        localDataSource.insertAlert(alert)

        // When
        localDataSource.deleteAlert(alert)
        val result = localDataSource.getAllAlerts().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun getAlerts_whenDatabaseEmpty_returnsEmptyList() = runTest {
        // When
        val result = localDataSource.getAllAlerts().first()

        // Then
        assertTrue(result.isEmpty())
    }
}