package com.example.brizzy.presentation.settings.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.brizzy.data.weather.datasource.local.dataStore.SettingsPreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val prefsManager: SettingsPreferencesManager) : ViewModel() {

    val tempUnit: StateFlow<String> = prefsManager.tempUnitFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Celsius"
    )

    val windUnit: StateFlow<String> = prefsManager.windUnitFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "m/s"
    )

    val locationMethod: StateFlow<String> = prefsManager.locationMethodFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "GPS"
    )

    val language: StateFlow<String> = prefsManager.languageFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "English"
    )

    fun updateTempUnit(unit: String) = viewModelScope.launch { prefsManager.saveTempUnit(unit) }
    fun updateWindUnit(unit: String) = viewModelScope.launch { prefsManager.saveWindUnit(unit) }
    fun updateLocationMethod(method: String) = viewModelScope.launch { prefsManager.saveLocationMethod(method) }
    fun updateLanguage(lang: String) = viewModelScope.launch { prefsManager.saveLanguage(lang) }
}

class SettingsViewModelFactory(private val prefsManager: SettingsPreferencesManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(prefsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}