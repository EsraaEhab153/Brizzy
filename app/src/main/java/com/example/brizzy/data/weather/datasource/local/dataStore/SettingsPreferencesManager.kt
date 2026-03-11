package com.example.brizzy.data.weather.datasource.local.dataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings_prefs")

class SettingsPreferencesManager(private val context: Context) {

    companion object {
        val TEMP_UNIT_KEY = stringPreferencesKey("temp_unit")
        val WIND_UNIT_KEY = stringPreferencesKey("wind_unit")
        val LOCATION_METHOD_KEY = stringPreferencesKey("location_method")
        val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    val tempUnitFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[TEMP_UNIT_KEY] ?: "Celsius"
    }

    val windUnitFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[WIND_UNIT_KEY] ?: "m/s"
    }

    val locationMethodFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LOCATION_METHOD_KEY] ?: "GPS"
    }

    val languageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "English"
    }

    suspend fun saveTempUnit(unit: String) {
        context.dataStore.edit { preferences -> preferences[TEMP_UNIT_KEY] = unit }
    }

    suspend fun saveWindUnit(unit: String) {
        context.dataStore.edit { preferences -> preferences[WIND_UNIT_KEY] = unit }
    }

    suspend fun saveLocationMethod(method: String) {
        context.dataStore.edit { preferences -> preferences[LOCATION_METHOD_KEY] = method }
    }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences -> preferences[LANGUAGE_KEY] = language }
    }
}