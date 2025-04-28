package dev.nalamzap.weatheringwithyou.localDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_table")
data class WeatherEnt(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val temperature: Double,
    val condition: String,
    val timestamp: Long
)