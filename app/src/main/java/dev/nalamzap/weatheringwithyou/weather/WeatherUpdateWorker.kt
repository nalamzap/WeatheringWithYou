package dev.nalamzap.weatheringwithyou.weather

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
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
                        condition = newWeather.condition,
                        iconUrl = newWeather.conditionIcon
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

    private suspend fun showNotification(
        city: String,
        temperature: Double,
        condition: String,
        iconUrl: String
    ) {
        val channelId = "weather_updates_channel"
        val name = "Weather Updates"
        val descriptionText = "Shows periodic weather updates"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val bitmap = withContext(Dispatchers.IO) {
            Glide.with(context)
                .asBitmap()
                .load("https:$iconUrl")
                .submit()
                .get()
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(IconCompat.createWithBitmap(bitmap))
            .setContentTitle("Weather in $city")
            .setContentText("$temperature°C, $condition")
            .setLargeIcon(bitmap)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setColor(context.getColor(R.color.teal_700))
            .build()

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(1001, notification)
        }
    }
}