package com.example.weatherwithretrofit.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.weatherwithretrofit.Common.Common
import com.example.weatherwithretrofit.R
import com.example.weatherwithretrofit.adapter.DailyAdapter
import com.example.weatherwithretrofit.databinding.FragmentTemperatureBinding
import com.example.weatherwithretrofit.model.Temp
import com.example.weatherwithretrofit.model.Weather
import com.example.weatherwithretrofit.retrofit.SearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TemperatureFragment: Fragment(R.layout.fragment_temperature) {

    private lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: DailyAdapter
    private val KEY = "O6OsWZoZuJ8oqxQ6vNZm6ykTCAYV6WHT"
    private val viewBinding: FragmentTemperatureBinding by viewBinding(FragmentTemperatureBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (arguments?.getSerializable("key") as? Weather)?.let { weather ->
            json(weather.Key, weather.LocalizedName)
            daily(weather.Key)
        }
    }

    //метод для вывода показаний на день
    private fun json(key: String, city: String){
        val mService: SearchService = Common.topCityService
        mService.getDaily(key).enqueue(object : Callback<Temp>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Temp>, response: Response<Temp>) {
                val body = response.body()
                viewBinding.day.text = "Днем: ${body?.DailyForecasts?.firstOrNull()?.Day?.IconPhrase}"
                viewBinding.night.text = "Ночью: ${body?.DailyForecasts?.firstOrNull()?.Night?.IconPhrase}"
                viewBinding.maxMin.text = "${body?.DailyForecasts?.firstOrNull()?.Temperature?.Maximum?.valueInC}°/${body?.DailyForecasts?.firstOrNull()?.Temperature?.Minimum?.valueInC}°"
                viewBinding.tbCity.title = city
                viewBinding.status.text = body?.Headline?.Text
                viewBinding.temperature.text = "${body?.DailyForecasts?.firstOrNull()?.Temperature?.Minimum?.valueInC}°C"
            }

            override fun onFailure(call: Call<Temp>, t: Throwable) {
                Log.e("ERROR_CONNECTION", "Ответ с сервера не получен")
            }

        })
    }

    //вывод показаний на 5 дней
    private fun daily(key: String){
        viewBinding.fiveDay.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        viewBinding.fiveDay.layoutManager = layoutManager
        val mService: SearchService = Common.topCityService
        mService.getFiveDay(key, key = KEY).enqueue(object : Callback<Temp>{
            override fun onResponse(call: Call<Temp>, response: Response<Temp>) {
                adapter = DailyAdapter(response.body() as Temp)
                adapter.notifyDataSetChanged()
                viewBinding.fiveDay.adapter = adapter
            }

            override fun onFailure(call: Call<Temp>, t: Throwable) {
                Log.e("CONNECTION_ERROR", "Не получен ответ от сервера")
            }

        })

    }
}