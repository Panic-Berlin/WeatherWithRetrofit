package com.example.weatherwithretrofit.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.weatherwithretrofit.Common.Common
import com.example.weatherwithretrofit.R
import com.example.weatherwithretrofit.adapter.WeatherAdapter
import com.example.weatherwithretrofit.databinding.FragmentWaeatherBinding
import com.example.weatherwithretrofit.model.Weather
import com.example.weatherwithretrofit.retrofit.SearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherFragment: Fragment(R.layout.fragment_waeather) {

    private val KEY = "O6OsWZoZuJ8oqxQ6vNZm6ykTCAYV6WHT"
    private lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: WeatherAdapter
    private val viewBinding: FragmentWaeatherBinding by viewBinding(FragmentWaeatherBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Говнокод для сокрытия и расскрытия верхнего бара по нажатию на searchTollBar
        viewBinding.searchToolBar.setOnClickListener {
            getAllWeatherList()
            viewBinding.searchToolBar.visibility = View.GONE
            viewBinding.tollBar.visibility = View.VISIBLE
        }

        //Поиск городов по мере ввода текста в etSearch
        viewBinding.etSearch.addTextChangedListener {
            getAllSearchList(viewBinding.etSearch.text.toString())
        }

        getAllWeatherList()

        //Раскрытие верхнего бара для поиска
        viewBinding.btnSeacrh.setOnClickListener(){
            if (viewBinding.tollBar.visibility == View.VISIBLE){
                viewBinding.tollBar.visibility = View.GONE
                viewBinding.searchToolBar.visibility = View.VISIBLE
            }
            else{
                viewBinding.tollBar.visibility = View.VISIBLE
                viewBinding.searchToolBar.visibility = View.GONE
                viewBinding.tollBar.visibility = View.VISIBLE
            }
        }
    }

    //Метод для вывода то 50-ти городов
    private fun getAllWeatherList(){

        val mService: SearchService = Common.topCityService
        viewBinding.topRV.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity)
        viewBinding.topRV.layoutManager = layoutManager
        mService.getWeatherList().enqueue(object : Callback<MutableList<Weather>>{
            override fun onResponse(
                call: Call<MutableList<Weather>>,
                response: Response<MutableList<Weather>>
            ) {
                adapter = WeatherAdapter(response.body() as MutableList<Weather>){
                    findNavController().navigate(R.id.action_weatherFragment_to_temperatureFragment, bundleOf(
                        "key" to it
                    ))
                }
                adapter.notifyDataSetChanged()
                viewBinding.topRV.adapter = adapter
            }
            override fun onFailure(call: Call<MutableList<Weather>>, t: Throwable) {
                Log.e("CONNECTION ERROR", "Неизвестная ошибка")
            }

        })
    }

    //Метод для вывода списка по поиску
    private fun getAllSearchList(want: String){
        val mService: SearchService = Common.topCityService
        viewBinding.topRV.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity)
        viewBinding.topRV.layoutManager = layoutManager
        mService.getSearchList(key = KEY, q = want).enqueue(object : Callback<MutableList<Weather>>{
            override fun onResponse(
                call: Call<MutableList<Weather>>,
                response: Response<MutableList<Weather>>
            ) {
                adapter = WeatherAdapter(response.body() as MutableList<Weather>){
                    findNavController().navigate(R.id.action_weatherFragment_to_temperatureFragment, bundleOf(
                        "key" to it
                    ))
                }
                adapter.notifyDataSetChanged()
                viewBinding.topRV.adapter = adapter
            }
            override fun onFailure(call: Call<MutableList<Weather>>, t: Throwable) {
                Log.e("ERROR_CONNECTION", "Ответ с сервера не получен")
            }

        })
    }
}