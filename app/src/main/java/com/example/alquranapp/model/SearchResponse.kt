package com.example.alquranapp.model
data class SearchResponse(
    val data: SearchData
)
data class SearchData(
    val count: Int,
    val matches: List<MatchedAyah>
)
data class MatchedAyah(
    val number: Int,
    val text: String,
    val surah: Surah,
    val numberInSurah: Int
)
