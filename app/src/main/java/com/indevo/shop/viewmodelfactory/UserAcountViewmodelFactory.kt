package com.indevo.shop.viewmodelfactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.indevo.shop.viewmodel.OrderViewModel
import com.indevo.shop.viewmodel.UserAcountViewModel

class UserAcountViewmodelFactory (private val firestore: FirebaseFirestore, private val auth: FirebaseAuth, private val storage: StorageReference, private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserAcountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserAcountViewModel(firestore,auth,storage,app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}