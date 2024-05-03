package com.indevo.shop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.data.User
import com.indevo.shop.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _user.emit(Resource.Error(error.message.toString()))
                    }
                } else {
                    val user = value?.toObject(User::class.java)
                    user?.let {
                        viewModelScope.launch {
                            _user.emit(Resource.Sucess(user))
                        }
                    }
                }
            }
    }

    fun logout(){
        auth.signOut()
    }

}