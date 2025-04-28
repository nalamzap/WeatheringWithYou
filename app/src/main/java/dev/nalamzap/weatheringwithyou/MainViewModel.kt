package dev.nalamzap.weatheringwithyou

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.nalamzap.weatheringwithyou.localDB.WeatherEnt
import dev.nalamzap.weatheringwithyou.weather.WeatherRepo
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: WeatherRepo,
    private val cityPref: String
) : ViewModel() {

    var city by mutableStateOf(cityPref)
        private set

    var weatherState by mutableStateOf<WeatherEnt?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadWeather()
    }

    fun updateCity(newCity: String) {
        city = newCity
        loadWeather(getOffline = false)
    }

    private fun loadWeather(getOffline: Boolean = true) {
        viewModelScope.launch {
            isLoading = true
            try {
                if (getOffline) {
                    val cached = repository.getCachedWeather()
                    weatherState = cached
                }

                repository.fetchAndSaveWeather(city)

                weatherState = repository.getCachedWeather()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
}