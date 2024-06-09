package com.example.weatherapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.weatherapp.data.local.WeatherEntity
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.viewmodel.WeatherViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class WeatherViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: WeatherRepository
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = WeatherViewModel(repository)

    }

    @Test
    fun `fetchWeather sets city name and calls repository`() = runTest {
        // Mock data
        val cityName = "Mumbai"
        val apiKey = "b92994729905f4e84e2804fa19241534"

        // Call the function
        viewModel.fetchWeather(cityName, apiKey)

        testDispatcher.scheduler.advanceUntilIdle()

        // Verify
        coVerify { repository.fetchWeatherFromApi(cityName, apiKey) }
    }

    @Test
    fun `getWeather is called with correct city name`() = runTest {
        // Mock data
        val cityName = "Mumbai"
        val weatherData = mockk<WeatherEntity>()

        // Stub repository function
        coEvery { repository.getWeather(cityName) } returns MutableLiveData(weatherData)

        // Observer for weather LiveData
        val weatherObserver = mockk<Observer<WeatherEntity>>(relaxed = true)
        viewModel.weather.observeForever(weatherObserver)

        // Update cityName LiveData directly
        viewModel.cityName.value = cityName

        // Verify
        coVerify { repository.getWeather(cityName) }

    }

    @Test
    fun `fetchWeather sets errorMessage on failure`() = runTest {
        // Mockk data
        val cityName = "InvalidCity"
        val apiKey = "testApiKey"
        val exceptionMessage =
            "Error occurred while fetching the weather details. Please try with other place."
        //Check the behaviour of the suspend function
        coEvery { repository.fetchWeatherFromApi(cityName, apiKey) } returns Result.failure(
            Exception(exceptionMessage)
        )

        //Observe the error message
        val errorMessageObserver = mockk<Observer<String?>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorMessageObserver)

        viewModel.fetchWeather(cityName, apiKey)
        testDispatcher.scheduler.advanceUntilIdle()

        //Verify
        coVerify { repository.fetchWeatherFromApi(cityName, apiKey) }
        verify { errorMessageObserver.onChanged(exceptionMessage) }

        viewModel.errorMessage.removeObserver(errorMessageObserver)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}