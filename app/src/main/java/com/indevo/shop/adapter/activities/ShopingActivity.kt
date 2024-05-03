package com.indevo.shop.adapter.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.R
import com.indevo.shop.databinding.ActivityShopingBinding
import com.indevo.shop.firebase.Firebasecommon
import com.indevo.shop.utils.Resource
import com.indevo.shop.viewmodel.CartViewModel
import com.indevo.shop.viewmodelfactory.CartViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class ShopingActivity : AppCompatActivity() {
        private lateinit var binding:ActivityShopingBinding
        private lateinit var viewModel : CartViewModel


        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityShopingBinding.inflate(layoutInflater)
        setContentView(binding.root)
                viewModel = ViewModelProvider(
                        this,
                        CartViewModelFactory(
                                FirebaseFirestore.getInstance(),
                                FirebaseAuth.getInstance(),
                                Firebasecommon(
                                        FirebaseFirestore.getInstance(),
                                        FirebaseAuth.getInstance())
                        )
                ).get(CartViewModel::class.java)

val navController=findNavController(R.id.shoppingHostFragment)
binding.bottomNavigation.setupWithNavController(navController)

                lifecycleScope.launchWhenStarted {
                        viewModel.cartProducts.collectLatest {
                                when (it) {
                                        is Resource.Sucess -> {
                                                val count = it.data?.size ?: 0
                                                val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
                                                bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                                                        number = count
                                                        backgroundColor = resources.getColor(R.color.g_blue)
                                                }
                                        }
                                        else -> Unit
                                }
                        }
                }

    }
}