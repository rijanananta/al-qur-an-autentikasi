package com.example.alquranapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.alquranapp.login.LoginScreen
import com.example.alquranapp.nav.QuranNavGraph
import com.example.alquranapp.notif.AlarmScheduler
import com.example.alquranapp.notif.NotificationHelper
import com.example.alquranapp.ui.theme.AlQuranAppTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            NotificationHelper.createChannel(this)
            AlarmScheduler.scheduleDailyReminder(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            NotificationHelper.createChannel(this)
            AlarmScheduler.scheduleDailyReminder(this)
        }

        setContent {
            AlQuranAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val context = androidx.compose.ui.platform.LocalContext.current
    var user by remember { mutableStateOf<FirebaseUser?>(auth.currentUser) }

    // Pantau status login Firebase
    LaunchedEffect(Unit) {
        auth.addAuthStateListener { firebaseAuth ->
            user = firebaseAuth.currentUser
        }
    }

    if (user == null) {
        LoginScreen(
            onLoginSuccess = { loggedInUser ->
                user = loggedInUser
            }
        )
    } else {
        QuranNavGraph(
            navController = navController,
            user = user!!,
            onLogout = {
                // Logout Firebase
                auth.signOut()

                // Logout Google
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInClient.signOut().addOnCompleteListener {
                    user = null // trigger UI ke login screen
                }
            }
        )
    }
}
