package com.indevo.shop.fragment.categories

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager

import androidx.recyclerview.widget.LinearLayoutManager

import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.R
import com.indevo.shop.adapter.BestDealsAdapter
import com.indevo.shop.adapter.BestProductsAdapter

import com.indevo.shop.adapter.SpecialProductsAdapter
import com.indevo.shop.databinding.FragmentMainCategoryBinding
import com.indevo.shop.utils.Resource
import com.indevo.shop.utils.showBottomNavigationView

import com.indevo.shop.viewmodel.MainCategoryViewModel
import com.indevo.shop.viewmodelfactory.MainCategoryViewmodelFactory
import kotlinx.coroutines.flow.collectLatest
private val TAG = "MainCategoryFragment"

class MainCategoryFragment : Fragment() {
        private lateinit var binding: FragmentMainCategoryBinding
        private lateinit var specialProductsAdapter:SpecialProductsAdapter
        private lateinit var bestDealsAdapter:BestDealsAdapter
        private lateinit var bestProductsAdapter:BestProductsAdapter
        private lateinit var viewModel:MainCategoryViewModel


        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentMainCategoryBinding.inflate(inflater)
            return binding.root
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, MainCategoryViewmodelFactory(FirebaseFirestore.getInstance())).get(MainCategoryViewModel::class.java)

        setupSpecialProductsRv()
        setupBestDealsRv()
        setupBestProducts()

        specialProductsAdapter.onClick= {
            val b = Bundle().apply { putParcelable("product", it) }

            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }
            bestProductsAdapter.onClick={
                val temp=Bundle().apply { putParcelable("product",it) }

                findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,temp)
            }
            bestDealsAdapter.onClick={
                val temp= Bundle().apply { putParcelable("product",it) }
                findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,temp)
            }


        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Sucess -> {
                        specialProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Sucess -> {
                        bestDealsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.bestProductsProgressbar.visibility = View.VISIBLE
                    }
                    is Resource.Sucess -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        binding.bestProductsProgressbar.visibility = View.GONE


                    }
                    is Resource.Error -> {
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.bestProductsProgressbar.visibility = View.GONE

                    }
                    else -> Unit
                }
            }
        }
    }


    private fun setupSpecialProductsRv() {
            specialProductsAdapter = SpecialProductsAdapter()
            binding.rvSpecialProducts.apply {
                layoutManager =LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

                adapter = specialProductsAdapter
            }
        }

    private fun setupBestDealsRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

    private fun setupBestProducts() {
        bestProductsAdapter = BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }



    private fun hideLoading() {
        binding.mainCategoryProgressbar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressbar.visibility = View.VISIBLE

    }

        override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}