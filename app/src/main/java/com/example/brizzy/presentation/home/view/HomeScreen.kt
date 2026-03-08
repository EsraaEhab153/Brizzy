package com.example.brizzy.presentation.home.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.brizzy.data.weather.model.ForecastItem
import com.example.brizzy.presentation.home.viewModel.HomeViewModel
import com.example.brizzy.utils.UiState
import kotlin.math.roundToInt
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.annotation.RawRes
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.example.brizzy.R

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getWeatherData(lat = 30.7865, lon = 31.0004)
    }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F7ED0), Color(0xFF145AB2))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        when (uiState) {
            is UiState.Loading -> {
//
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.weather))

                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )

                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier
                        .size(130.dp)
                        .align(alignment = Alignment.Center)
                )
            }
            is UiState.Error -> {
                val error = (uiState as UiState.Error).message
                Text(text = error, color = Color.Red, modifier = Modifier.align(Alignment.Center))
            }
            is UiState.Success -> {
                val weatherData = (uiState as UiState.Success).data
                val currentWeather = weatherData.list[0]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 45.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = weatherData.city.name,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    val animResId = getWeatherLottieAnim(currentWeather.weather[0].icon)

                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animResId))
                    val progress by animateLottieCompositionAsState(
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                    )

                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(150.dp)
                    )

//                    val mainIconUrl = "https://openweathermap.org/img/wn/${currentWeather.weather[0].icon}@4x.png"
//                    AsyncImage(
//                        model = mainIconUrl,
//                        contentDescription = "Weather Icon",
//                        modifier = Modifier.size(150.dp)
//                    )

                    Text(
                        text = "${currentWeather.main.temp.roundToInt()}°",
                        color = Color.White,
                        fontSize = 80.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = currentWeather.weather[0].description.replaceFirstChar { it.uppercase() },
                        color = Color.White,
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        WeatherChip("↑ ${currentWeather.main.tempMax.roundToInt()}°")
                        WeatherChip("↓ ${currentWeather.main.tempMin.roundToInt()}°")
                        WeatherChip("Feels ${currentWeather.main.feelsLike.roundToInt()}°")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    WeatherDetailsGrid(currentWeather)

                    Spacer(modifier = Modifier.height(32.dp))

                    val hourlyList = weatherData.list.take(8)
                    HourlyForecastSection(hourlyList)

                    Spacer(modifier = Modifier.height(32.dp))

                      val dailyList = weatherData.list.groupBy { it.dtTxt.substringBefore(" ") }
                        .map { it.value.first() }
                        .take(5)

                    DailyForecastSection(dailyList)

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun WeatherChip(text: String) {
    Surface(
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
fun WeatherDetailsGrid(currentWeather: ForecastItem) {
    Surface(
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DetailItem("Humidity", "${currentWeather.main.humidity}%")
                DetailItem("Wind", "${currentWeather.wind.speed} m/s")
                DetailItem("Pressure", "${currentWeather.main.pressure} hPa")
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DetailItem("Clouds", "${currentWeather.clouds.all}%")
                DetailItem("Visibility", "${currentWeather.visibility / 1000} km")
                DetailItem("Feels Like", "${currentWeather.main.feelsLike.roundToInt()}°C")
            }
        }
    }
}

@Composable
fun DetailItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}

@Composable
fun HourlyForecastSection(hourlyList: List<ForecastItem>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hourly Forecast",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(hourlyList) { item ->
                HourlyItem(item)
            }
        }
    }
}

@Composable
fun HourlyItem(item: ForecastItem) {
    Surface(
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.width(80.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val time = SimpleDateFormat("h a", Locale.getDefault()).format(Date(item.dt * 1000))

            Text(text = time, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            val animResId = getWeatherLottieAnim(item.weather[0].icon)

            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animResId))
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever
            )
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(70.dp),
             contentScale = ContentScale.Fit
            )
//            val iconUrl = "https://openweathermap.org/img/wn/${item.weather[0].icon}@2x.png"
//            AsyncImage(
//                model = iconUrl,
//                contentDescription = null,
//                modifier = Modifier.size(40.dp)
//            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${item.main.temp.roundToInt()}°",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DailyForecastSection(dailyList: List<ForecastItem>) {
    Surface(
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "5-Day Forecast",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            dailyList.forEach { item ->
                DailyItem(item)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DailyItem(item: ForecastItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(item.dt * 1000))

        Text(text = dayName, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))

        val animResId = getWeatherLottieAnim(item.weather[0].icon)

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animResId))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(50.dp),
            contentScale = ContentScale.Fit
        )


//        val iconUrl = "https://openweathermap.org/img/wn/${item.weather[0].icon}@2x.png"
//        AsyncImage(
//            model = iconUrl,
//            contentDescription = null,
//            modifier = Modifier
//                .size(40.dp)
//                .weight(0.5f)
//        )

        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.weight(1f)) {
            Text(text = "${item.main.tempMax.roundToInt()}°", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "${item.main.tempMin.roundToInt()}°", color = Color.White.copy(alpha = 0.6f))
        }
    }
}

@RawRes
fun getWeatherLottieAnim(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> R.raw.sunny
        "01n" -> R.raw.weather_night


        "02d" -> R.raw.cloudy_day
        "02n" -> R.raw.weather_cloudy_night

        "03d", "03n",
        "04d", "04n" -> R.raw.weather_windy


        "09d", "09n" -> R.raw.rain_icon

        "10d" -> R.raw.weather_partly_shower
        "10n" -> R.raw.weather_rainy_night

        "11d", "11n" -> R.raw.weather_storm

        "13d", "13n" -> R.raw.snowing

        "50d", "50n" -> R.raw.mist

        else -> R.raw.weather_windy
    }
}