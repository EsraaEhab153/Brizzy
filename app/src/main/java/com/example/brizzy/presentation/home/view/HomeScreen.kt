package com.example.brizzy.presentation.home.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.example.brizzy.R
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.brizzy.data.weather.model.City
import com.example.brizzy.presentation.theme.WeatherThemeState


@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    val currentTempUnit by viewModel.tempUnit.collectAsState()
    val currentWindUnit by viewModel.windUnit.collectAsState()

    LaunchedEffect(Unit) {
      viewModel.getWeatherData(lat = 30.7865, lon = 31.0004)
    }

    val iconCode = if (uiState is UiState.Success) {
        (uiState as UiState.Success).data.list[0].weather[0].icon
    } else null

    val targetColors = getWeatherBackgroundColors(iconCode)

    val topColor by animateColorAsState(
        targetValue = targetColors[0],
        animationSpec = tween(durationMillis = 1000),
        label = "Top Color Animation"
    )
    val bottomColor by animateColorAsState(
        targetValue = targetColors[1],
        animationSpec = tween(durationMillis = 1000),
        label = "Bottom Color Animation"
    )
    WeatherThemeState.currentColors = listOf(topColor, bottomColor)

    val animatedGradient = Brush.verticalGradient(listOf(topColor, bottomColor))
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedGradient)
    ) {
        when (uiState) {
            is UiState.Loading -> {
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

                val dailyList = weatherData.list.groupBy { it.dtTxt.substringBefore(" ") }
                    .map { entry ->
                        val dayItems = entry.value
                        val dailyMax = dayItems.maxOf { it.main.tempMax }
                        val dailyMin = dayItems.minOf { it.main.tempMin }
                        val firstItem = dayItems.first()
                        firstItem.copy(
                            main = firstItem.main.copy(tempMax = dailyMax, tempMin = dailyMin)
                        )
                    }
                    .take(5)

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

                    Spacer(modifier = Modifier.height(8.dp))
                    val currentDateTime =
                        SimpleDateFormat("EEE, MMM d • hh:mm a", Locale.getDefault()).format(
                            Date(System.currentTimeMillis())
                        )

                    Text(
                        text = currentDateTime,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    val animResId = getWeatherLottieAnim(currentWeather.weather[0].icon)

                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(
                            animResId
                        )
                    )
                    val progress by animateLottieCompositionAsState(
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                    )

                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(150.dp)
                    )

                    Text(
                        text = formatTemperature(currentWeather.main.temp, currentTempUnit),
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
                        WeatherChip("↑ ${formatTemperature(dailyList[0].main.tempMax, currentTempUnit)}")
                        WeatherChip("↓ ${formatTemperature(dailyList[0].main.tempMin, currentTempUnit)}")
                        WeatherChip("Feels ${formatTemperature(currentWeather.main.feelsLike, currentTempUnit)}")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    WeatherDetailsGrid(currentWeather = currentWeather, city = weatherData.city,currentTempUnit=currentTempUnit,currentWindUnit=currentWindUnit)

                    Spacer(modifier = Modifier.height(32.dp))

                    val hourlyList = weatherData.list.take(8)
                    HourlyForecastSection(hourlyList,currentTempUnit)

                    Spacer(modifier = Modifier.height(32.dp))

                    DailyForecastSection(dailyList,currentTempUnit)

                    Spacer(modifier = Modifier.height(60.dp))
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
fun WeatherDetailsGrid(currentWeather: ForecastItem,city: City,currentTempUnit:String,currentWindUnit:String) {
    val sunriseTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(city.sunrise * 1000))
    val sunsetTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(city.sunset * 1000))

    Surface(
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WeatherDetailChip(
                    iconRes = R.drawable.humidity,
                    title = "Humidity",
                    value = "${currentWeather.main.humidity}%",
                    modifier = Modifier.weight(1f)
                )
                WeatherDetailChip(
                    iconRes = R.drawable.windy,
                    title = "Wind",
                    value = formatWindSpeed(currentWeather.wind.speed, currentWindUnit),
                    modifier = Modifier.weight(1f)
                )
                WeatherDetailChip(
                    iconRes = R.drawable.barometer,
                    title = "Pressure",
                    value = "${currentWeather.main.pressure} hPa",
                    modifier = Modifier.weight(1f)
                )
                WeatherDetailChip(
                    iconRes = R.drawable.cloud,
                    title = "Clouds",
                    value = "${currentWeather.clouds.all}%",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WeatherDetailChip(
                    iconRes = R.drawable.visible,
                    title = "Visibility",
                    value = "${currentWeather.visibility / 1000} km",
                    modifier = Modifier.weight(1f)
                )
                WeatherDetailChip(
                    iconRes = R.drawable.thermostat,
                    title = "Feels Like",
                    value = formatTemperature(currentWeather.main.feelsLike, currentTempUnit),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WeatherDetailChip(iconRes = R.drawable.sunrise, title = "Sunrise", value = sunriseTime, modifier = Modifier.weight(1f))
               WeatherDetailChip(iconRes = R.drawable.dawn, title = "Sunset", value = sunsetTime, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun WeatherDetailChip(
    @DrawableRes iconRes: Int,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 10.sp,
                maxLines = 1
            )
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
fun HourlyForecastSection(hourlyList: List<ForecastItem>,currentTempUnit:String) {
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
                HourlyItem(item,currentTempUnit)
            }
        }
    }
}

@Composable
fun HourlyItem(item: ForecastItem,currentTempUnit:String) {
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatTemperature(item.main.temp,currentTempUnit),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DailyForecastSection(dailyList: List<ForecastItem>,currentTempUnit:String) {
    Column(modifier = Modifier.padding(0.dp)) {
        Text(
            text = "5-Day Forecast",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Surface(
            color = Color.White.copy(alpha = 0.15f),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                dailyList.forEach { item ->
                    DailyItem(item,currentTempUnit)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun DailyItem(item: ForecastItem,currentTempUnit:String) {
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
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.weight(1f)) {
            Text(
                text = formatTemperature(item.main.tempMax,currentTempUnit),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formatTemperature(item.main.tempMin,currentTempUnit),
                color = Color.White.copy(alpha = 0.6f)
            )
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

fun getWeatherBackgroundColors(iconCode: String?): List<Color> {
    return when (iconCode) {
        "01d" -> listOf(Color(0xFF29B2DD), Color(0xFF33AADD))

        "01n" -> listOf(Color(0xFF08244F), Color(0xFF134CB5))

        "02d", "03d", "04d" -> listOf(Color(0xFF5983A8), Color(0xFF86A8C9))

        "02n", "03n", "04n" -> listOf(Color(0xFF1E2836), Color(0xFF304052))

        "09d", "10d", "11d", "50d" -> listOf(Color(0xFF4A5E6D), Color(0xFF677E90))

        "09n", "10n", "11n", "50n" -> listOf(Color(0xFF141E30), Color(0xFF243B55))

        "13d", "13n" -> listOf(Color(0xFF6A93CB), Color(0xFFA4BFE3))

        else -> listOf(Color(0xFF2B70E4), Color(0xFF0F2B6B))
    }
}


fun formatTemperature(tempInCelsius: Double, unit: String): String {
    return when (unit) {
        "Fahrenheit" -> "${((tempInCelsius * 9 / 5) + 32).roundToInt()}°f"
        "Kelvin" -> "${(tempInCelsius + 273.15).roundToInt()}K"
        else -> "${tempInCelsius.roundToInt()}°c"
    }
}

fun formatWindSpeed(speedInMs: Double, unit: String): String {
    return when (unit) {
        "mph" -> "${(speedInMs * 2.23694).roundToInt()} mph"
        else -> "${speedInMs.roundToInt()} m/s"
    }
}