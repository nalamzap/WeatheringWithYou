package dev.nalamzap.weatheringwithyou.weather

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.nalamzap.weatheringwithyou.App
import dev.nalamzap.weatheringwithyou.R
import dev.nalamzap.weatheringwithyou.feature_preference.PreferenceActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherUpdateWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val weatherRepo = App.instance.weatherRepo

        val city = context.getSharedPreferences("WeatheringWithYou", Context.MODE_PRIVATE)
            .getString(PreferenceActivity.CITY, null) ?: return Result.failure()

        return withContext(Dispatchers.IO) {
            try {
                val newWeather = weatherRepo.fetchAndSaveWeather(city)
                if (newWeather != null) {
                    showNotification(
                        city = city,
                        temperature = newWeather.temperature,
                        condition = newWeather.condition
                    )
                    Result.success()
                } else {
                    Result.retry()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.retry()
            }
        }
    }

    private fun showNotification(city: String, temperature: Double, condition: String) {
        val channelId = "weather_updates_channel"
        val name = "Weather Updates"
        val descriptionText = "Shows periodic weather updates"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.round_device_thermostat_24)
            .setContentTitle("🌤️ Weather in $city")
            .setContentText("$temperature°C, $condition")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Current temperature in $city is $temperature°C with $condition.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Notification disappears on tap
            .setColor(context.getColor(R.color.teal_700)) // Color accent (optional)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(1001, notification)
        }
    }
}