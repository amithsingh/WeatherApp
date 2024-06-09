package com.example.weatherapp.data.repository

import androidx.lifecycle.LiveData
import com.example.weatherapp.data.local.WeatherDao
import com.example.weatherapp.data.local.WeatherEntity
import com.example.weatherapp.data.remote.WeatherService
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class WeatherRepository(private val weatherDao: WeatherDao, private val api: WeatherService) {

    fun getWeather(city: String): LiveData<WeatherEntity> {
        return weatherDao.getWeather(city)
    }

    suspend fun fetchWeatherFromApi(city: String, apiKey: String): Result<WeatherEntity> {
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
            Result.failure(IOException("Error occurred while fetching the weather details. Please try with other place."))
        } catch (e: UnknownHostException) {
            Result.failure(IOException("Network error. Please check your internet connection."))
        } catch (e: SocketTimeoutException) {
            Result.failure(IOException("Request timed out. Please try again."))
        } catch (e: IOException) {
            Result.failure(IOException("An error occurred. Please try again."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}