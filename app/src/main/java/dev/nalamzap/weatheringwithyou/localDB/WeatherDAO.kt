package dev.nalamzap.weatheringwithyou.localDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEnt)

    @Query("SELECT * FROM weather_table ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestWeather(): WeatherEnt?

    @Query("DELETE FROM weather_table")
    suspend fun deleteAll()

}