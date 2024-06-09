package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.remote.RetrofitInstance
import com.example.weatherapp.data.repository.WeatherRepository

class MyApp : Application() {
    private val database by lazy { WeatherDatabase.getDatabase(this) }
    val repository by lazy { WeatherRepository(database.weatherDao(), RetrofitInstance.api) }

}