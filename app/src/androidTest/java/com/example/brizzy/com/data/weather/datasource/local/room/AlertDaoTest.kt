package com.example.brizzy.com.data.weather.datasource.local.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.brizzy.data.db.WeatherDatabase
import com.example.brizzy.data.weather.datasource.local.room.AlertDao
import com.example.brizzy.data.weather.model.AlertEntity
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

@RunWith(AndroidJUnit4::class)
class AlertDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var dao: AlertDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.alertDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAlert_cairoCity_returnsAlertInFlow() = runTest {
        // Given
        val alert = AlertEntity(id = 1, cityName = "Cairo", lat = 30.0, lon = 31.0, startTime = 1000L, endTime = 2000L, isNotification = true)

        // When
        dao.insertAlert(alert)
        val alertsList = dao.getAllAlerts().first()

        // Then
        assertThat(1, `is`(alertsList.size))
        assertThat("Cairo", `is`(alertsList[0].cityName))
    }

    @Test
    fun deleteAlert_removesAlertFromFlow() = runTest {
        // Given
        val alert = AlertEntity(id = 1, cityName = "Alex", lat = 31.0, lon = 29.0, startTime = 1000L, endTime = 2000L, isNotification = false)
        dao.insertAlert(alert)

        // When
        dao.deleteAlert(alert)
        val alertsList = dao.getAllAlerts().first()

        // Then
        assertTrue(alertsList.isEmpty())
    }

    @Test
    fun getAlerts_whenDatabaseIsEmpty_returnsEmptyList() = runTest {
        // When
        val alertsList = dao.getAllAlerts().first()

        // Then
        assertTrue(alertsList.isEmpty())
    }
}