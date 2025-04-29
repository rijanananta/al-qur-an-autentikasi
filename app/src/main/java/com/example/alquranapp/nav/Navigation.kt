package com.example.alquranapp.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alquranapp.screens.DetailJuzScreen
import com.example.alquranapp.screens.DetailSurahScreen
import com.example.alquranapp.screens.ListSurahScreen
import com.example.alquranapp.screens.ProfileScreen
import com.example.alquranapp.screens.SearchScreen
import com.google.firebase.auth.FirebaseUser

@Composable
fun QuranNavGraph(
    navController: NavHostController,
    user: FirebaseUser,
    onLogout: () -> Unit
) {
    NavHost(navController, startDestination = "list") {
        composable("list") {
            ListSurahScreen(navController = navController, user = user, onLogout = onLogout)
        }
        composable("detail/{surahNumber}") { backStackEntry ->
            val surahNumber = backStackEntry.arguments?.getString("surahNumber")?.toIntOrNull() ?: 1
            DetailSurahScreen(surahNumber)
        }
        composable("juzDetail/{juzNumber}") { backStackEntry ->
            val juzNumber = backStackEntry.arguments?.getString("juzNumber")?.toIntOrNull() ?: 1
            DetailJuzScreen(juzNumber = juzNumber)
        }
        composable("search") {
            SearchScreen()
        }
        composable("profile") {
            ProfileScreen(user = user, onLogout = onLogout)
        }
    }
}
