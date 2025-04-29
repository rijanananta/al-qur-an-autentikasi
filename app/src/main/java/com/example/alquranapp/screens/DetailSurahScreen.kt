package com.example.alquranapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.alquranapp.model.Ayah
import com.example.alquranapp.network.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSurahScreen(surahNumber: Int) {
    val context = LocalContext.current
    var arabAyat by remember { mutableStateOf<List<Ayah>>(emptyList()) }
    var indoAyat by remember { mutableStateOf<List<Ayah>>(emptyList()) }
    var surahTitle by remember { mutableStateOf("") }
    var surahType by remember { mutableStateOf("") }
    var surahTranslation by remember { mutableStateOf("") }
    var ayahCount by remember { mutableStateOf(0) }
    var playingAyatIndex by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()
    val player = remember { ExoPlayer.Builder(context).build() }
    val listState = rememberLazyListState()

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    val currentIndex = playingAyatIndex
                    if (currentIndex != null && currentIndex + 1 < arabAyat.size) {
                        val nextAyah = arabAyat[currentIndex + 1]
                        val nextAudioUrl =
                            "https://cdn.alquran.cloud/media/audio/ayah/ar.alafasy/${nextAyah.number}"
                        player.setMediaItem(MediaItem.fromUri(nextAudioUrl))
                        player.prepare()
                        player.play()
                        playingAyatIndex = currentIndex + 1

                        scope.launch {
                            delay(300)
                            listState.animateScrollToItem(currentIndex + 1)
                        }
                    } else {
                        playingAyatIndex = null
                    }
                }
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitInstance.api.getSurahDetail(surahNumber)
                val arab = response.data[0]
                val indo = response.data[1]
                arabAyat = arab.ayahs
                indoAyat = indo.ayahs
                surahTitle = arab.englishName
                surahTranslation = arab.englishNameTranslation
                surahType = when (arab.revelationType) {
                    "Meccan" -> "Makkiyah"
                    "Medinan" -> "Madaniyah"
                    else -> "Tidak diketahui"
                }
                ayahCount = arab.ayahs.size
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = surahTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        if (surahType.isNotEmpty()) {
                            Text(
                                text = "$surahType • $surahTranslation • $ayahCount Ayat",
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            LazyColumn(state = listState) {
                itemsIndexed(arabAyat) { index, arabAyah ->
                    val indoAyah = indoAyat.getOrNull(index)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Ayat ${arabAyah.numberInSurah}",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = arabAyah.text,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = indoAyah?.text ?: "Terjemahan tidak tersedia",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = {
                                        val audioUrl =
                                            "https://cdn.alquran.cloud/media/audio/ayah/ar.alafasy/${arabAyah.number}"

                                        if (playingAyatIndex != index) {
                                            player.setMediaItem(MediaItem.fromUri(audioUrl))
                                            player.prepare()
                                            player.play()
                                            playingAyatIndex = index

                                            scope.launch {
                                                delay(300)
                                                listState.animateScrollToItem(index)
                                            }
                                        } else {
                                            player.pause()
                                            playingAyatIndex = null
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (playingAyatIndex == index) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (playingAyatIndex == index) "Pause Ayat" else "Putar Ayat"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
