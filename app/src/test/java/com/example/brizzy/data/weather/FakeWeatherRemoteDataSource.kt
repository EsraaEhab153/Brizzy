package com.example.brizzy.data.weather

import com.example.brizzy.data.weather.datasource.remote.WeatherRemoteDataSource
import com.example.brizzy.data.weather.model.GeocodingResponseItem
import com.example.brizzy.data.weather.model.WeatherResponse
import org.mockito.Mockito.mock
import retrofit2.Response

class FakeWeatherRemoteDataSource : WeatherRemoteDataSource {

    override suspend fun getWeatherOverNetwork(
        lat: Double,
        lon: Double
    ): Response<WeatherResponse> {
         val fakeWeather = mock(WeatherResponse::class.java)
        return Response.success(fakeWeather)
    }

    override suspend fun searchCity(
        cityName: String,
        apiKey: String
    ): Response<List<GeocodingResponseItem>> {
        return Response.success(emptyList())
    }
}