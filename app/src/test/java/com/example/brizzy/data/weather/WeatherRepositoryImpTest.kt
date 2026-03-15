package com.example.brizzy.data.weather

import com.example.brizzy.data.weather.model.AlertEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.example.brizzy.data.weather.datasource.local.room.IWeatherLocalDataSource
import kotlinx.coroutines.flow.Flow
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class WeatherRepositoryImpTest {

    val alert1 = AlertEntity(id = 1, cityName = "Cairo", lat = 30.0, lon = 31.0, startTime = 0L, endTime = 0L, isNotification = true)
    val alert2 = AlertEntity(id = 2, cityName = "Alex", lat = 31.0, lon = 29.0, startTime = 0L, endTime = 0L, isNotification = false)
    val alert3 = AlertEntity(id = 3, cityName = "Aswan", lat = 24.0, lon = 32.0, startTime = 0L, endTime = 0L, isNotification = true)

    val localListOfAlerts = mutableListOf(alert1, alert2)

    lateinit var fakeLocalDataSource: FakeWeatherLocalDataSource
    lateinit var repository: WeatherRepositoryImp
    lateinit var fakeRemoteDataSource: FakeWeatherRemoteDataSource

    @Before
    fun setup() {
        fakeLocalDataSource = FakeWeatherLocalDataSource(localListOfAlerts)
        fakeRemoteDataSource = FakeWeatherRemoteDataSource()
        repository = WeatherRepositoryImp(fakeRemoteDataSource, fakeLocalDataSource)}

    @Test
    fun getAllAlerts_noUpdate_returnsLocalAlerts() = runTest {
        // When
        val result = repository.getAllAlerts().first()

        // Then
        assertThat(result, `is`(localListOfAlerts))
        assertThat(result.size, `is`(2))
    }

    @Test
    fun insertAlert_addsAlertToLocalDataSource() = runTest {
        // When
        repository.insertAlert(alert3)
        val result = repository.getAllAlerts().first()

        // Then
        assertThat(result.contains(alert3), `is`(true))
        assertThat(result.size, `is`(3))
    }

    @Test
    fun deleteAlert_removesAlertFromLocalDataSource() = runTest {
        // When
        repository.deleteAlert(alert1)
        val result = repository.getAllAlerts().first()

        // Then
        assertThat(result.contains(alert1), `is`(false))
        assertThat(result.size, `is`(1))
    }
}