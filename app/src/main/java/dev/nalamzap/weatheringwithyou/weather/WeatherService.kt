package dev.nalamzap.weatheringwithyou.weather

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") cityName: String,
        @Query("aqi") aqi: String = "yes"
    ): Response<ResponseBody>
}