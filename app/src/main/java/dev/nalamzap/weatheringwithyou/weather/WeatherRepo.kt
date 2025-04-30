package dev.nalamzap.weatheringwithyou.weather

import android.util.Log
import dev.nalamzap.weatheringwithyou.localDB.WeatherDao
import dev.nalamzap.weatheringwithyou.localDB.WeatherEnt
import org.json.JSONObject

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
                val responseString = weatherResponse.string()
                val jsonObject = JSONObject(responseString)
                val weatherEnt = WeatherEnt(
                    temperature = jsonObject.getJSONObject("current")
                        .getDouble("temp_c"),
                    condition = jsonObject.getJSONObject("current")
                        .getJSONObject("condition").getString("text"),
                    conditionIcon = jsonObject.getJSONObject("current")
                        .getJSONObject("condition").getString("icon"),
                    timestamp = jsonObject.getJSONObject("location")
                        .getLong("localtime_epoch")
                )
                weatherDao.deleteAll()
                weatherDao.insertWeather(weatherEnt)
                return weatherEnt
            }
        } else {
            if (response.code() == 400) {
                throw Exception("Did you get the city wrong?\nCheck your spelling and try again.")
            } else if (response.code() == 403) {
                throw Exception("Are you using the correct API key?")
            } else {
                throw Exception("Network call failed: ${response.code()}")
            }
        }
        return null
    }
}