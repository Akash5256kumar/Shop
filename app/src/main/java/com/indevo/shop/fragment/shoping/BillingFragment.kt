package com.indevo.shop.fragment.shoping

import android.app.AlertDialog
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.R
import com.indevo.shop.adapter.AddressAdapter
import com.indevo.shop.adapter.BillingProductsAdapter
import com.indevo.shop.data.Address
import com.indevo.shop.data.CartProduct
import com.indevo.shop.data.ordered.Order
import com.indevo.shop.data.ordered.OrderStatus
import com.indevo.shop.databinding.FragmentBillingBinding
import com.indevo.shop.utils.HorizontalItemDecoration
import com.indevo.shop.utils.Resource
import com.indevo.shop.viewmodel.AddressViewModel
import com.indevo.shop.viewmodel.BillingViewModel
import com.indevo.shop.viewmodel.OrderViewModel
import com.indevo.shop.viewmodelfactory.AdressViewModelFactory
import com.indevo.shop.viewmodelfactory.BillingViewModelFcatory
import com.indevo.shop.viewmodelfactory.OredredViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class BillingFragment : Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private lateinit var billingViewModel :BillingViewModel
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f

    private var selectedAddress: Address? = null
    private lateinit var orderViewModel :OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        products = args.products.toList()
        totalPrice = args.totalPrice



        Log.d("data",products.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        billingViewModel = ViewModelProvider(this, BillingViewModelFcatory(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())).get(BillingViewModel::class.java)

        orderViewModel = ViewModelProvider(this, OredredViewModelFactory(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())).get(OrderViewModel::class.java)
        setupBillingProductsRv()
        setupAddressRv()


        if (!args.payment) {
            binding.apply {
                buttonPlaceOrder.visibility = View.INVISIBLE
                totalBoxContainer.visibility = View.INVISIBLE
                middleLine.visibility = View.INVISIBLE
                bottomLine.visibility = View.INVISIBLE
            }
        }

        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        addressAdapter.onClick = {
            selectedAddress = it
            if (!args.payment) {
                val b = Bundle().apply { putParcelable("address", selectedAddress) }
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment, b)
            }
        }


        lifecycleScope.launchWhenStarted {
            billingViewModel.address.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Sucess -> {
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(), "Error ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonPlaceOrder.startAnimation()
                    }

                    is Resource.Sucess -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(), "Your order was placed", Snackbar.LENGTH_LONG)
                            .show()
                    }

                    is Resource.Error -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        Toast.makeText(requireContext(), "Error ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit
                }
            }
        }


        billingProductsAdapter.differ.submitList(products)


        binding.tvTotalPrice.text = "$ $totalPrice"


        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                Toast.makeText(requireContext(), "Please select and address", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }

    }

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order items")
            setMessage("Do you want to order your cart items?")
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes") { dialog, _ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    products,
                    selectedAddress!!
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }


    private fun setupAddressRv() {
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setupBillingProductsRv() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = billingProductsAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}