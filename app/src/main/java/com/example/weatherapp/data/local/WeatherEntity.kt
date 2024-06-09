package com.example.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "weather_table")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityName: String,
    val temperature: Float,
    val description: String,
    val humidity: Int,
    val windSpeed: Float
)