package com.example.brizzy.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.brizzy.R
import com.example.brizzy.data.db.WeatherDatabase
import com.example.brizzy.data.weather.WeatherRepositoryImp
import com.example.brizzy.data.weather.datasource.local.room.WeatherLocalDataSource
import com.example.brizzy.data.weather.datasource.remote.WeatherRemoteDataSourceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first // 💡 الـ import ده مهم جداً عشان الـ first()
import kotlinx.coroutines.withContext

class WeatherWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val lat = inputData.getDouble("lat", 0.0)
                val lon = inputData.getDouble("lon", 0.0)
                val isNotification = inputData.getBoolean("isNotification", true)
                val cityName = inputData.getString("cityName") ?: "Unknown"
                val startTime = inputData.getLong("startTime", 0L)
                val endTime = inputData.getLong("endTime", 0L)

                val currentTime = System.currentTimeMillis()

                if (currentTime in startTime..endTime) {

                    val database = WeatherDatabase.getDatabase(applicationContext)
                    val localDataSource = WeatherLocalDataSource(database.favoriteLocationDao(), database.alertDao())
                    val remoteDataSource = WeatherRemoteDataSourceImpl()
                    val repository = WeatherRepositoryImp(remoteDataSource, localDataSource)
                    val forecastResponse = repository.getWeather(lat, lon).first()

                    var shouldAlert = false
                    var alertMessage = ""

                    for (forecast in forecastResponse.list) {

                        val forecastTime = forecast.dt * 1000L
                        if (forecastTime in startTime..endTime) {
                            val weatherId = forecast.weather.firstOrNull()?.id ?: 800
                            if (weatherId <= 800) {
                                shouldAlert = true
                                val description = forecast.weather.firstOrNull()?.description ?: "bad weather"
                                alertMessage = "Warning! $description expected in $cityName."
                                break
                            }
                        }
                    }

                    if (shouldAlert) {
                        sendNotification(cityName, alertMessage, isNotification)
                    } else {
                        Log.d("WeatherWorker", "Weather is clear for $cityName, no alert needed.")
                    }

                } else if (currentTime > endTime) {
                    Log.d("WeatherWorker", "Alert expired for $cityName. Time has passed.")
                }

                Result.success()
            } catch (e: Exception) {
                Log.e("WeatherWorker", "Worker Failed or No Internet: ${e.message}")
                Result.retry()
            }
        }
    }

    private fun sendNotification(cityName: String, message: String, isNotification: Boolean) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = if (isNotification) "WEATHER_NOTIFY_CHANNEL" else "WEATHER_ALARM_CHANNEL"
        val channelName = if (isNotification) "Weather Notifications" else "Weather Alarms"

        val soundUri = if (isNotification) {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for weather warnings"
                setSound(soundUri, null)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.bell)
            .setContentTitle(if (isNotification) "Weather Info" else "🚨 WEATHER ALARM 🚨")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}