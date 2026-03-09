package com.example.brizzy.data.weather

import com.example.brizzy.data.weather.datasource.remote.WeatherRemoteDataSource
import com.example.brizzy.data.weather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImp(
    private val remoteDataSource: WeatherRemoteDataSource
) : WeatherRepository {

    override suspend fun getWeather(lat: Double, lon: Double): Flow<WeatherResponse> {
        return flow {
            val response = remoteDataSource.getWeatherOverNetwork(lat, lon)

            if (response.isSuccessful) {
                response.body()?.let { weatherResponse ->
                    emit(weatherResponse)
                }
            } else {
                throw Exception("Error fetching data: ${response.message()}")
            }
        }
    }
}