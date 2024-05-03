package com.indevo.shop.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.viewmodel.MainCategoryViewModel



class MainCategoryViewmodelFactory( private val firestore: FirebaseFirestore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainCategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainCategoryViewModel(firestore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}