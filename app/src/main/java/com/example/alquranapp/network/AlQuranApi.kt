package com.example.alquranapp.network

import com.example.alquranapp.model.JuzResponse
import com.example.alquranapp.model.SearchResponse
import com.example.alquranapp.model.SurahResponse
import com.example.alquranapp.model.SurahEditionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AlQuranApi {
    @GET("surah")
    suspend fun getAllSurah(): SurahResponse
    @GET("surah/{number}/editions/quran-simple,id.indonesian")
    suspend fun getSurahDetail(
        @Path("number") number: Int
    ): SurahEditionResponse
    @GET("juz/{juzNumber}/ar.alafasy")
    suspend fun getJuzDetail(
        @Path("juzNumber") juzNumber: Int
    ): Response<JuzResponse>
    @GET("search/{query}/all/id.indonesian")
    suspend fun searchAyah(@Path("query") query: String): Response<SearchResponse>
}
