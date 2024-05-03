package com.indevo.shop.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.indevo.shop.viewmodel.LoginViewModel

class LoginViewMOdelFactory(private val firebaseAuth: FirebaseAuth) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(firebaseAuth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
