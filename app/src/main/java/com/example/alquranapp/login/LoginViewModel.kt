package com.example.alquranapp.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alquranapp.auth.FirebaseAuthManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _user = MutableStateFlow<FirebaseUser?>(FirebaseAuthManager.currentUser)
    val user: StateFlow<FirebaseUser?> = _user

    fun logout() {
        viewModelScope.launch {
            FirebaseAuthManager.signOut()
            _user.value = null
        }
    }

    fun updateUser(user: FirebaseUser) {
        _user.value = user
    }
}
