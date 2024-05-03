package com.indevo.shop.viewmodelfactory

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.data.Category
import com.indevo.shop.viewmodel.CategoryViewModel
import com.indevo.shop.viewmodel.IntroductionViewModel

class CategoryViewModelFactory (private val firestore: FirebaseFirestore,private val category: Category) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(firestore,category) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}