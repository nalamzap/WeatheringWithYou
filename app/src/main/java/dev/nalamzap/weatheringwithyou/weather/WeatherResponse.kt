package dev.nalamzap.weatheringwithyou.weather

data class WeatherResponse(
    val location: Location,
    val current: Current
)

data class Location(
    val name: String,
    val localtime_epoch: Long
)

data class Current(
    val temp_c: Double,
    val condition: Condition
)

data class Condition(
    val text: String
)