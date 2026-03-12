package com.example.brizzy.presentation.favorite.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.brizzy.data.weather.WeatherRepository
import com.example.brizzy.data.weather.model.FavoriteLocationEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: WeatherRepository) : ViewModel() {

    val favoriteLocations: StateFlow<List<FavoriteLocationEntity>> =
        repository.getAllFavoriteLocations()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    var locationToDelete = mutableStateOf<FavoriteLocationEntity?>(null)
    var showConfirmDialog = mutableStateOf(false)

    fun requestDelete(location: FavoriteLocationEntity) {
        locationToDelete.value = location
        showConfirmDialog.value = true
    }

    fun confirmDelete() {
        locationToDelete.value?.let { location ->
            viewModelScope.launch {
                repository.deleteFavoriteLocation(location)
            }
        }
        resetDeleteState()
    }

    fun cancelDelete() {
        resetDeleteState()
    }

    private fun resetDeleteState() {
        showConfirmDialog.value = false
        locationToDelete.value = null
    }
}

class FavoritesViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}