package com.indevo.shop.fragment.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.manager.Lifecycle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.R
import com.indevo.shop.data.Category
import com.indevo.shop.utils.Resource
import com.indevo.shop.viewmodel.CategoryViewModel
import com.indevo.shop.viewmodelfactory.CategoryViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChairFragment :BaseCategoryFragment() {

    private lateinit var viewModel:CategoryViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this,CategoryViewModelFactory(FirebaseFirestore.getInstance(),Category.Chair)).get(CategoryViewModel::class.java)
        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showOfferLoading()

                    }
                    is Resource.Sucess->{
                        offerAdapter.differ.submitList(it.data)
                        hideOfferLoading()


                    }
                    is Resource.Error->{
                        Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_LONG)
                        hideOfferLoading()
                    }
                    else ->Unit

            }

        }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showOfferLoading()
                    }
                    is Resource.Sucess->{
                        bestProductsAdapter.differ.submitList(it.data)
                        hideOfferLoading()
                    }
                    is Resource.Error->{
                        Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_LONG)
                        hideOfferLoading()

                    }
                    else->Unit
                }
            }
        }


    }
    override fun onBestProductsPagingRequest() {

    }

    override fun onOfferPagingRequest() {


    }
}