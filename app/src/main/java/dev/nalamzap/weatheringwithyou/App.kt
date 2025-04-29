package dev.nalamzap.weatheringwithyou

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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

    private lateinit var connectivityManager: ConnectivityManager

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
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
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

    fun isNetworkAvailable(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            ?: return false
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    companion object {
        lateinit var instance: App
            private set
    }
}