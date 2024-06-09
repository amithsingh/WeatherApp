package com.example.weatherapp

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.data.local.WeatherDao
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/*Unit test case for Room Database to check the data is inserted and get operations*/
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RoomDatabaseTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var weatherDao: WeatherDao
    private lateinit var db: WeatherDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        weatherDao = db.weatherDao()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetWeather() = runTest {
        // Mock data
        val city = "TestCity"
        val weather = WeatherEntity(1, city, 25.0f, "Sunny", 70, 5.0f)

        // Insert weather data
        weatherDao.insert(weather)

        // Observer to wait for LiveData to emit
        val latch = CountDownLatch(1)
        val observer = androidx.lifecycle.Observer<WeatherEntity> {
            latch.countDown()

            // Verify the emitted weather data matches the inserted data
            assertEquals(city, it.cityName)
            assertEquals(25.0, it.temperature.toDouble(), 0.001)
            assertEquals("Sunny", it.description)
            assertEquals(70, it.humidity)
            assertEquals(5.0, it.windSpeed.toDouble(), 0.001)

        }

        // Observe the weather data
        weatherDao.getWeather(city).observeForever(observer)

        // Wait for the LiveData to emit
        latch.await(2, TimeUnit.SECONDS)
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        db.close()
    }
}