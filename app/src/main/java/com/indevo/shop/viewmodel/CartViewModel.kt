package com.indevo.shop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.data.CartProduct
import com.indevo.shop.firebase.Firebasecommon
import com.indevo.shop.helpher.getProductPrice
import com.indevo.shop.utils.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: Firebasecommon
) : ViewModel() {

    private val _cartProducts =
        MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()


    val productsPrice = cartProducts.map {
        when (it) {
            is Resource.Sucess -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }
    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    private var cartProductDocuments = emptyList<DocumentSnapshot>()


    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("cart")
                .document(documentId).delete()
        }
    }


    private fun calculatePrice(data: List<CartProduct>): Float {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price) * cartProduct.quantity).toDouble()
        }.toFloat()
    }


    init {
        getCartProducts()
    }


    private fun getCartProducts() {
        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch { _cartProducts.emit(Resource.Error(error?.message.toString())) }
                } else {
                    cartProductDocuments = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.Sucess(cartProducts)) }
                }
            }
    }


    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: Firebasecommon.QuantityChanging
    ) {

        val index = cartProducts.value.data?.indexOf(cartProduct)

        /**
         * index could be equal to -1 if the function [getCartProducts] delays which will also delay the result we expect to be inside the [_cartProducts]
         * and to prevent the app from crashing we make a check
         */
        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            when (quantityChanging) {
                Firebasecommon.QuantityChanging.INCREASE -> {
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    increaseQuantity(documentId)
                }
                Firebasecommon.QuantityChanging.DECREASE -> {
                    if (cartProduct.quantity == 1) {
                        viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                        return
                    }
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) { result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId) { result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }


}