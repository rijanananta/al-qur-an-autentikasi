package com.example.alquranapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alquranapp.model.Surah
import com.example.alquranapp.network.RetrofitInstance
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.ButtonDefaults



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListSurahScreen(
    navController: NavController,
    user: FirebaseUser,
    onLogout: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("📖 Surah", "📚 Juz")
    var surahList by remember { mutableStateOf<List<Surah>>(emptyList()) }

    LaunchedEffect(Unit) {
        val result = withContext(Dispatchers.IO) {
            RetrofitInstance.api.getAllSurah()
        }
        surahList = result.data ?: emptyList()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Al-Qur'an Digital") },
                actions = {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.Default.Search, contentDescription = "Cari Ayat")
                    }
                    IconButton(onClick = { navController.navigate("profile") }) {
                        user.photoUrl?.let { photoUrl ->
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Foto Profil",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(MaterialTheme.shapes.small)
                            )
                        } ?: Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil Akun"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            when (selectedTabIndex) {
                0 -> SurahTab(surahList, navController)
                1 -> JuzTab(navController)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(user: FirebaseUser, onLogout: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profil") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            user.photoUrl?.let { photoUrl ->
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Foto Profil",
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Anda login sebagai:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = user.displayName ?: "Nama tidak tersedia",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Email: ${user.email ?: "Email tidak tersedia"}",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 6.dp)
            ) {
                Text("Logout")
            }

        }
    }
}

@Composable
fun SurahTab(surahList: List<Surah>, navController: NavController) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(surahList) { surah ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { navController.navigate("detail/${surah.number}") },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = "Icon Surah",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${surah.number}. ${surah.englishName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = surah.englishNameTranslation,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = translateRevelationType(surah.revelationType),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = surah.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "${surah.numberOfAyahs} Ayat",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun JuzTab(navController: NavController) {
    val totalJuz = 30
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(totalJuz) { index ->
            val juzNumber = index + 1
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        navController.navigate("juzDetail/$juzNumber")
                    },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = "Icon Juz",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Text("Juz $juzNumber", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

fun translateRevelationType(type: String): String {
    return when (type.lowercase()) {
        "meccan" -> "Makkiyah"
        "medinan" -> "Madaniyah"
        else -> type
    }
}
