package com.example.brizzy.presentation.settings.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.brizzy.data.weather.datasource.local.dataStore.SettingsPreferencesManager
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var prefsManager: SettingsPreferencesManager
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        prefsManager = mockk(relaxed = true)

        every { prefsManager.tempUnitFlow } returns flowOf("Celsius")
        every { prefsManager.windUnitFlow } returns flowOf("m/s")
        every { prefsManager.locationMethodFlow } returns flowOf("GPS")
        every { prefsManager.languageFlow } returns flowOf("English")
        every { prefsManager.mapLatFlow } returns flowOf(30.0444)
        every { prefsManager.mapLonFlow } returns flowOf(31.2357)

        viewModel = SettingsViewModel(prefsManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun init_loadsPreferencesCorrectly() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.tempUnit.value, `is`("Celsius"))
        assertThat(viewModel.language.value, `is`("English"))
    }

    @Test
    fun updateTempUnit_callsSaveTempUnit() = runTest {
        // Given
        val newUnit = "Fahrenheit"
        coEvery { prefsManager.saveTempUnit(newUnit) } just Runs

        // When
        viewModel.updateTempUnit(newUnit)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { prefsManager.saveTempUnit(newUnit) }
    }

    @Test
    fun updateLanguage_callsSaveLanguage() = runTest {
        // Given
        val newLang = "Arabic"
        coEvery { prefsManager.saveLanguage(newLang) } just Runs

        // When
        viewModel.updateLanguage(newLang)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { prefsManager.saveLanguage(newLang) }
    }

    @Test
    fun updateMapLocation_callsSaveMapLocation() = runTest {
        // Given
        val lat = 31.2001
        val lon = 29.9187
        coEvery { prefsManager.saveMapLocation(lat, lon) } just Runs

        // When
        viewModel.updateMapLocation(lat, lon)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { prefsManager.saveMapLocation(lat, lon) }
    }
}