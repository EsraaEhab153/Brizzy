package com.example.brizzy.data.weather.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class WeatherResponse(
    val list: List<ForecastItem>,
    val city: City
)

data class ForecastItem(
    val dt: Long,
    val main: MainParams,
    val weather: List<WeatherDetails>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    @SerializedName("dt_txt")
    val dtTxt: String
)

data class MainParams(
    val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    val pressure: Int,
    val humidity: Int
)

data class WeatherDetails(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(
    val all: Int
)

data class Wind(
    val speed: Double
)

data class City(
    val name: String,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)