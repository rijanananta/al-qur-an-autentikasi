package com.example.alquranapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alquranapp.model.MatchedAyah
import com.example.alquranapp.network.RetrofitInstance
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var searchResults by remember { mutableStateOf<List<MatchedAyah>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Cari ") },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                singleLine = true,
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFF388E3C),
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        searchResults = emptyList()
                        try {
                            val response = RetrofitInstance.api.searchAyah(query.text)
                            if (response.isSuccessful) {
                                val result = response.body()
                                searchResults = result?.data?.matches ?: emptyList()
                            } else {
                                errorMessage = "Gagal memuat data: ${response.code()}"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Terjadi kesalahan: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
            ) {
                Icon(Icons.Default.Search, contentDescription = "Cari", tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Cari", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        when {
            isLoading -> CircularProgressIndicator()
            errorMessage != null -> Text(
                text = errorMessage ?: "Error",
                color = MaterialTheme.colorScheme.error
            )
            searchResults.isEmpty() && query.text.isNotBlank() -> Text("Tidak ditemukan hasil.")
            else -> {
                LazyColumn {
                    items(searchResults) { ayah ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "📖 ${ayah.surah.englishName} (${ayah.surah.name}) - Ayat ${ayah.numberInSurah}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = ayah.text,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
