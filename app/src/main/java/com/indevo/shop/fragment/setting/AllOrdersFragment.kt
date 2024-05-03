package com.indevo.shop.fragment.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.R
import com.indevo.shop.adapter.AllOrdersAdapter
import com.indevo.shop.databinding.FragmentAllOrdersBinding
import com.indevo.shop.utils.Resource
import com.indevo.shop.viewmodel.AddressViewModel
import com.indevo.shop.viewmodel.AllOrdersViewModel
import com.indevo.shop.viewmodelfactory.AdressViewModelFactory
import com.indevo.shop.viewmodelfactory.AllOredredViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class AllOrdersFragment : Fragment() {
    private lateinit var binding: FragmentAllOrdersBinding
    private lateinit var viewModel:AllOrdersViewModel
    val ordersAdapter by lazy { AllOrdersAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllOrdersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, AllOredredViewModelFactory(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())).get(AllOrdersViewModel::class.java)
        setupOrdersRv()

        lifecycleScope.launchWhenStarted {
            viewModel.allOrders.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAllOrders.visibility = View.VISIBLE
                    }
                    is Resource.Sucess -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        ordersAdapter.differ.submitList(it.data)
                        if (it.data.isNullOrEmpty()) {
                            binding.tvEmptyOrders.visibility = View.VISIBLE
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.progressbarAllOrders.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }

        binding.imageCloseOrders.setOnClickListener {
            findNavController().navigate(R.id.action_allOrdersFragment_to_profileFragment)

        }
        ordersAdapter.onClick = {
            val action = AllOrdersFragmentDirections.actionAllOrdersFragmentToOrderDetailFragment(it)
            findNavController().navigate(action)
        }


    }

    private fun setupOrdersRv() {
        binding.rvAllOrders.apply {
            adapter = ordersAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }
}