package com.indevo.shop.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.data.Category
import com.indevo.shop.firebase.Firebasecommon
import com.indevo.shop.viewmodel.CartViewModel
import com.indevo.shop.viewmodel.CategoryViewModel

class CartViewModelFactory (private val firestore: FirebaseFirestore, private val auth: FirebaseAuth, private val firebaseCommon: Firebasecommon) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(firestore,auth,firebaseCommon) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}