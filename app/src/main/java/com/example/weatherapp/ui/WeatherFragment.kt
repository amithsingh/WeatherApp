package com.example.weatherapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.weatherapp.MyApp
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.example.weatherapp.utils.Constants
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.example.weatherapp.viewmodel.WeatherViewModelFactory

class WeatherFragment : Fragment() {

    private lateinit var binding: FragmentWeatherBinding
    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory((requireActivity().application as MyApp).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.fetchWeatherButton.setOnClickListener {
            val city = binding.cityNameEditText.text.toString()
            if (city != "") {
                viewModel.fetchWeather(city, Constants.API_KEY)
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Enter city name field should not be empty!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            binding.errorMessageTextView.text = errorMessage
        }

        return binding.root
    }
}