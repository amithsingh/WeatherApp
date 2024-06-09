package com.example.weatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.WeatherEntity
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    val cityName = MutableLiveData<String>()

    val weather: LiveData<WeatherEntity> = cityName.switchMap { city ->
        repository.getWeather(city)
    }

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


    fun fetchWeather(city: String, apiKey: String) {
        cityName.value = city.trim().replaceFirstChar { it.uppercase() }
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val result = repository.fetchWeatherFromApi(city, apiKey)
                if (result.isFailure) {
                    _errorMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Unknown error occurred. Please try again after sometime."
//                    _errorMessage.value =
//                        "Enter invalid city name. Please try with other city."
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

}