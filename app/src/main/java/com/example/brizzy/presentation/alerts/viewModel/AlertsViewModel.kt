package com.example.brizzy.presentation.alerts.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.brizzy.data.weather.WeatherRepository
import com.example.brizzy.data.weather.model.AlertEntity
import com.example.brizzy.worker.WeatherWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AlertsViewModel(private val repository: WeatherRepository) : ViewModel() {

    val alerts: StateFlow<List<AlertEntity>> = repository.getAllAlerts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertAlert(alert: AlertEntity, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlert(alert)

            val inputData = Data.Builder()
                .putDouble("lat", alert.lat)
                .putDouble("lon", alert.lon)
                .putBoolean("isNotification", alert.isNotification)
                .putString("cityName", alert.cityName)
                .putLong("startTime", alert.startTime)
                .putLong("endTime", alert.endTime)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<WeatherWorker>(
                15, TimeUnit.MINUTES
            )
                .setInputData(inputData)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .addTag(alert.id.toString())
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "WeatherAlert_${alert.id}",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    fun deleteAlert(alert: AlertEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlert(alert)
        }
    }
}

class AlertsViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlertsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}