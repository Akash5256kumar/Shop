package com.indevo.shop.fragment.shoping

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.R
import com.indevo.shop.adapter.CartProductAdapter
import com.indevo.shop.databinding.FragmentCartBinding
import com.indevo.shop.firebase.Firebasecommon
import com.indevo.shop.utils.Resource
import com.indevo.shop.utils.VerticalItemDecoration
import com.indevo.shop.viewmodel.CartViewModel
import com.indevo.shop.viewmodel.DetailsViewModel
import com.indevo.shop.viewmodelfactory.CartViewModelFactory
import com.indevo.shop.viewmodelfactory.DetailsViewModelFactory
import kotlinx.coroutines.flow.collectLatest


class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductAdapter() }
    private lateinit var viewModel :CartViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        setupCartRv()

        var totalPrice = 0f
        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest { price ->
                price?.let {
                    totalPrice = it
                    binding.tvTotalPrice.text = "$ $price"
                }
            }
        }


        cartAdapter.onProductClick = {
            val b = Bundle().apply {
                putParcelable("product", it.product)
            }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, b)
        }

        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, Firebasecommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it, Firebasecommon.QuantityChanging.DECREASE)
        }
        binding.buttonCheckout.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice,cartAdapter.differ.currentList.toTypedArray(),true)
            findNavController().navigate(action)
        }



        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarCart.visibility = View.VISIBLE
                    }
                    is Resource.Sucess -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        if (it.data!!.isEmpty()) {
                            showEmptyCart()
                            hideOtherViews()
                        } else {
                            hideEmptyCart()
                            showOtherViews()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from cart")
                    setMessage("Do you want to delete this item from your cart?")
                    setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Yes") { dialog, _ ->
                        viewModel.deleteCartProduct(it)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }
    }
    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

            addItemDecoration( VerticalItemDecoration())
        }
    }

}