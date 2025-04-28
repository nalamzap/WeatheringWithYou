package dev.nalamzap.weatheringwithyou.localDB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WeatherEnt::class], version = 1, exportSchema = false)
abstract class WeatherDB: RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}