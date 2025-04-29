package com.example.alquranapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alquranapp.model.Ayah
import com.example.alquranapp.network.RetrofitInstance
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailJuzScreen(juzNumber: Int) {
    val context = LocalContext.current
    var ayahs by remember { mutableStateOf<List<Ayah>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(juzNumber) {
        isLoading = true
        scope.launch {
            try {
                val response = RetrofitInstance.api.getJuzDetail(juzNumber)
                if (response.isSuccessful) {
                    ayahs = response.body()?.data?.ayahs ?: emptyList()
                } else {
                    errorMessage = "Gagal memuat data Juz"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detail Juz $juzNumber") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                errorMessage != null -> Text(
                    text = errorMessage ?: "Terjadi kesalahan",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> {
                    val groupedAyat = ayahs.groupBy { it.surah.number }

                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        groupedAyat.forEach { (_, ayatList) ->
                            item {
                                Text(
                                    text = "📖 ${ayatList.first().surah.englishName} (${ayatList.first().surah.name})",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                            }
                            items(ayatList) { ayah ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Ayat ${ayah.numberInSurah}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = ayah.text,
                                            fontSize = 26.sp,
                                            lineHeight = 34.sp,
                                            textAlign = TextAlign.End,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        ayah.translation?.let {
                                            Text(
                                                text = "Arti: $it",
                                                fontSize = 20.sp,
                                                lineHeight = 28.sp,
                                                style = MaterialTheme.typography.bodyMedium
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
    }
}
