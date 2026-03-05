package com.example.brizzy.data.weather.datasource.remote

import com.example.brizzy.data.network.RetrofitClient
import com.example.brizzy.data.weather.model.WeatherResponse
import com.example.brizzy.utils.Constants
import retrofit2.Response

interface WeatherRemoteDataSource {
    suspend fun getWeatherOverNetwork(lat: Double, lon: Double): Response<WeatherResponse>
}

class WeatherRemoteDataSourceImpl : WeatherRemoteDataSource {

    private val weatherService = RetrofitClient.weatherService

    override suspend fun getWeatherOverNetwork(lat: Double, lon: Double): Response<WeatherResponse> {

        return weatherService.getWeatherForecast(
            lat = lat,
            lon = lon,
            apiKey = Constants.API_KEY,
            units = "metric",
            lang = "en"
        )
    }
}

