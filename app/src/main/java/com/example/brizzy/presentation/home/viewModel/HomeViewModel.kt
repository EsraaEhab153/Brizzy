package com.example.brizzy.presentation.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brizzy.data.weather.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import com.example.brizzy.data.weather.datasource.local.dataStore.SettingsPreferencesManager
import com.example.brizzy.data.weather.model.WeatherResponse
import com.example.brizzy.utils.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val repository: WeatherRepository,
    private val prefsManager: SettingsPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<WeatherResponse>>(UiState.Loading)
    val uiState: StateFlow<UiState<WeatherResponse>> = _uiState.asStateFlow()

    fun getWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                repository.getWeather(lat, lon)
                    .catch { e ->
                        _uiState.value = UiState.Error(e.message ?: "Unknown Error")
                    }
                    .collect { response ->
                        _uiState.value = UiState.Success(response)
                    }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Check your internet connection")
            }
        }
    }
    val tempUnit = prefsManager.tempUnitFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Celsius"
    )

    val windUnit = prefsManager.windUnitFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "m/s"
    )

    val latitude = prefsManager.mapLatFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 30.0444
    )

    val longitude = prefsManager.mapLonFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 31.2357
    )
}


//class HomeViewModelFactory(private val weatherRepository : WeatherRepository): ViewModelProvider.Factory{
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return HomeViewModel(weatherRepository) as T
//    }
//}

class HomeViewModelFactory(private val repository: WeatherRepository,
                           private val prefsManager: SettingsPreferencesManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository,prefsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}