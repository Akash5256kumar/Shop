package com.indevo.shop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.data.Address
import com.indevo.shop.utils.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddressViewModel  constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress = _addNewAddress.asStateFlow()


    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun addAddress(address: Address) {
        val validateInputs = validateInputs(address)

        if (validateInputs) {
            viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }
            firestore.collection("user").document(auth.uid!!).collection("address").document()
                .set(address).addOnSuccessListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Sucess(address)) }
                }.addOnFailureListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Error(it.message.toString())) }
                }
        } else {
            viewModelScope.launch {
                _error.emit("All fields are required")
            }
        }
    }



    private fun validateInputs(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty()
    }

}