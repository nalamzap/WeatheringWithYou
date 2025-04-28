package dev.nalamzap.weatheringwithyou.weather

import android.util.Log
import dev.nalamzap.weatheringwithyou.localDB.WeatherDao
import dev.nalamzap.weatheringwithyou.localDB.WeatherEnt

class WeatherRepo(
    private val weatherDao: WeatherDao,
    private val weatherService: WeatherService,
    private val apiKey: String
) {

    suspend fun getCachedWeather(): WeatherEnt? {
        return weatherDao.getLatestWeather()
    }

    suspend fun fetchAndSaveWeather(city: String): WeatherEnt? {
        val response = weatherService.getCurrentWeather(cityName = city, apiKey = apiKey)
        if (response.isSuccessful) {
            Log.d("WeatherRepo", "fetchAndSaveWeather: ${response.body()}")
            response.body()?.let { weatherResponse ->
                val weatherEnt = WeatherEnt(
                    temperature = weatherResponse.current.temp_c,
                    condition = weatherResponse.current.condition.text,
                    timestamp = weatherResponse.location.localtime_epoch
                )
                weatherDao.deleteAll()
                weatherDao.insertWeather(weatherEnt)
                return weatherEnt
            }
        } else {
            Log.d("WeatherRepo", "fetchAndSaveWeather: ${response.code()}")
            throw Exception("Network call failed: ${response.code()}")
        }
        return null
    }
}