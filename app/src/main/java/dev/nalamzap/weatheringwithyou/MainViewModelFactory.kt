package dev.nalamzap.weatheringwithyou

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.nalamzap.weatheringwithyou.weather.WeatherRepo

class MainViewModelFactory(
    private val repo: WeatherRepo,
    private val city: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repo, city) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}