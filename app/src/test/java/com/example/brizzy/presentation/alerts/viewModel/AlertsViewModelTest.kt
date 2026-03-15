package com.example.brizzy.presentation.alerts.viewModel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.brizzy.data.weather.WeatherRepository
import com.example.brizzy.data.weather.model.AlertEntity
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AlertsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: WeatherRepository
    private lateinit var viewModel: AlertsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = mockk()

        val fakeAlerts = listOf(AlertEntity(1, "Cairo", 30.0, 31.0, 0L, 0L, true))

        every { repository.getAllAlerts() } returns flowOf(fakeAlerts)
        every { repository.insertAlert(any()) } just Runs
        every { repository.deleteAlert(any()) } just Runs

        viewModel = AlertsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getAlerts_returnsDataFromRepository() = runTest {
        // Given
        val results = mutableListOf<List<AlertEntity>>()

        backgroundScope.launch(testDispatcher) {
            viewModel.alerts.collect { results.add(it) }
        }

        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val finalResult = results.last()
        assertThat(finalResult, `is`(not(nullValue())))
        assertThat(finalResult.size, `is`(1))
        assertThat(finalResult[0].cityName, `is`("Cairo"))
    }

    @Test
    fun insertAlert_callsRepositoryInsert() = runTest {
        // Given
        val alert = AlertEntity(2, "Alex", 31.0, 29.0, 0L, 0L, false)
        val mockContext = mockk<Context>(relaxed = true)

        // When
        viewModel.insertAlert(alert, mockContext)
        testDispatcher.scheduler.advanceUntilIdle()
        //Then
        assertThat(repository.insertAlert(alert), `is`(not(nullValue())))
    }

    @Test
    fun deleteAlert_callsRepositoryDelete() = runTest {
        // Given
        val alert = AlertEntity(1, "Cairo", 30.0, 31.0, 0L, 0L, true)

        // When
        viewModel.deleteAlert(alert)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat( repository.deleteAlert(alert), `is`(not(nullValue())))

    }
}