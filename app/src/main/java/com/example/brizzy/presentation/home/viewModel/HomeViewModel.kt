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

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun getWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            try {
                repository.getWeather(lat, lon)
                    .catch { e ->
                        _uiState.value = WeatherUiState.Error(e.message ?: "Unknown Error")
                    }
                    .collect { response ->
                        _uiState.value = WeatherUiState.Success(response)
                    }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "Check your internet connection")
            }
        }
    }
}


class HomeViewModelFactory(private val weatherRepository : WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(weatherRepository) as T
    }
}

//class HomeViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return HomeViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}