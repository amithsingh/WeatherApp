package com.example.weatherapp.data.repository

import androidx.lifecycle.LiveData
import com.example.weatherapp.data.local.WeatherDao
import com.example.weatherapp.data.local.WeatherEntity
import com.example.weatherapp.data.remote.WeatherService
import retrofit2.HttpException

class WeatherRepository(private val weatherDao: WeatherDao, private val api: WeatherService) {

    fun getWeather(city: String): LiveData<WeatherEntity> {
        return weatherDao.getWeather(city)
    }

    suspend fun fetchWeatherFromApi(city: String, apiKey: String): Result<WeatherEntity>{
       return try {
            val response = api.getWeather(city, apiKey)
            val weatherEntity = WeatherEntity(
                cityName = response.name,
                temperature = response.main.temp,
                description = response.weather[0].description,
                humidity = response.main.humidity,
                windSpeed = response.wind.speed
            )
            weatherDao.insert(weatherEntity)
           Result.success(weatherEntity)
       } catch (e: HttpException) {
           Result.failure(e)
       } catch (e: Exception) {
           Result.failure(e)
       }
    }
}