package com.example.brizzy.presentation.map.viewModel

import androidx.lifecycle.viewModelScope
import com.example.brizzy.data.weather.WeatherRepository
import com.example.brizzy.data.weather.model.GeocodingResponseItem
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MapViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<GeocodingResponseItem>>(emptyList())
    val searchResults: StateFlow<List<GeocodingResponseItem>> = _searchResults.asStateFlow()

    private val _typingFlow = MutableSharedFlow<String>()

    init {
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            _typingFlow
                .debounce(500)
                .filter { it.length > 2 }
                .distinctUntilChanged()
                .collectLatest { query ->
                    repository.searchCity(query).collect { results ->
                        _searchResults.value = results
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String, isSelectingFromList: Boolean = false) {
        _searchQuery.value = query

        if (isSelectingFromList) {
            _searchResults.value = emptyList()
        } else {
            viewModelScope.launch {
                _typingFlow.emit(query)
            }
            if (query.length <= 2) {
                _searchResults.value = emptyList()
            }
        }
    }
}

class MapViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}