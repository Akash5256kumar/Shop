package com.indevo.shop.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.viewmodel.AddressViewModel
import com.indevo.shop.viewmodel.AllOrdersViewModel

class AllOredredViewModelFactory (private val firestore: FirebaseFirestore, private val auth: FirebaseAuth) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllOrdersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AllOrdersViewModel   (firestore,auth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}