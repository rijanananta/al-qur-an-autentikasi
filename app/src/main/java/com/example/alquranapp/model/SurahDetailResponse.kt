package com.example.alquranapp.model

data class SurahEditionResponse(
    val data: List<SurahEditionData>
)
data class SurahEditionData(
    val name: String,
    val number: Int,
    val englishName: String,
    val englishNameTranslation: String,
    val revelationType: String,
    val numberOfAyahs: Int,
    val ayahs: List<Ayah>
)