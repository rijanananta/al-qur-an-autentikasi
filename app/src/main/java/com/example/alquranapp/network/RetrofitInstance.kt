package com.example.alquranapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: AlQuranApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.alquran.cloud/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AlQuranApi::class.java)
    }
}