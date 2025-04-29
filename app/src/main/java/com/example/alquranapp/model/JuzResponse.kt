package com.example.alquranapp.model

data class JuzResponse(
    val data: JuzData
)
data class JuzData(
    val ayahs: List<Ayah>
)
data class SurahInfo(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val revelationType: String
)
data class Ayah(
    val number: Int,
    val numberInSurah: Int,
    val text: String,
    val audio: String?,
    val surah: SurahInfo,
    val translation: String? = null
)
data class TranslatedAyah(
    val number: Int,
    val numberInSurah: Int,
    val text: String,
    val surah: SurahInfo
)
data class Audio(
    val primary: String
)
data class AyahDisplay(
    val numberInSurah: Int,
    val arabText: String,
    val translation: String,
    val surah: Surah
)
