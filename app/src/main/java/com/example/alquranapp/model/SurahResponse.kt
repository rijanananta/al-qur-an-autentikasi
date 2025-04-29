package com.example.alquranapp.model

data class SurahResponse(
    val data: List<Surah>
)
data class Surah(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val revelationType: String,
    val numberOfAyahs: Int
)
