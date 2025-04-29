package dev.nalamzap.weatheringwithyou

import android.app.Application
import androidx.room.Room
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.nalamzap.weatheringwithyou.localDB.WeatherDB
import dev.nalamzap.weatheringwithyou.weather.WeatherRepo
import dev.nalamzap.weatheringwithyou.weather.WeatherService
import dev.nalamzap.weatheringwithyou.weather.WeatherUpdateWorker
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class App: Application() {

    lateinit var weatherService: WeatherService
        private set

    lateinit var weatherDatabase: WeatherDB
        private set

    lateinit var weatherRepo: WeatherRepo
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiKey = BuildConfig.WEATHER_API_KEY

        weatherService = retrofit.create(WeatherService::class.java)

        weatherDatabase = Room.databaseBuilder(
            applicationContext,
            WeatherDB::class.java,
            "weather_db"
        ).build()

        weatherRepo = WeatherRepo(
            weatherDao = weatherDatabase.weatherDao(),
            weatherService = weatherService,
            apiKey = apiKey
        )

        scheduleWeatherUpdateWorker()
    }

    fun scheduleWeatherUpdateWorker() {
        val workRequest = PeriodicWorkRequestBuilder<WeatherUpdateWorker>(
            6, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(instance)
            .enqueueUniquePeriodicWork(
                "WeatherUpdateWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }

    companion object {
        lateinit var instance: App
            private set
    }
}