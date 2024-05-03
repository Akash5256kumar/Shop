package com.indevo.shop.fragment.shoping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.R
import com.indevo.shop.adapter.ColorsAdapter
import com.indevo.shop.adapter.SizesAdapter
import com.indevo.shop.adapter.ViewPager2Images
import com.indevo.shop.data.CartProduct
import com.indevo.shop.databinding.FragmentProductDetailsBinding
import com.indevo.shop.firebase.Firebasecommon
import com.indevo.shop.utils.Resource
import com.indevo.shop.utils.hideBottomNavigationView
import com.indevo.shop.viewmodel.DetailsViewModel
import com.indevo.shop.viewmodelfactory.DetailsViewModelFactory
import kotlinx.coroutines.flow.collectLatest


class ProductDetailsFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<DetailsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationView()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProvider(
            this,
            DetailsViewModelFactory(
                FirebaseFirestore.getInstance(),
                FirebaseAuth.getInstance(),
                Firebasecommon(FirebaseFirestore.getInstance(),
                    FirebaseAuth.getInstance())
            )
        ).get(DetailsViewModel::class.java)



        val product = args.product

        setupSizesRv()
        setupColorsRv()
        setupViewpager()

        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        sizesAdapter.onItemClick = {
            selectedSize = it
        }

        colorsAdapter.onItemClick = {
            selectedColor = it
        }
        binding.buttonAddToCart.setOnClickListener {
            viewModel.addUpdateProductInCart(CartProduct(product, 1, selectedColor, selectedSize))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonAddToCart.startAnimation()
                    }

                    is Resource.Sucess -> {
                        binding.buttonAddToCart.revertAnimation()
                        binding.buttonAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                        Toast.makeText(requireContext(), "Product add in Your Cart", Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Error -> {
                        binding.buttonAddToCart.stopAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "$ ${product.price}"
            tvProductDescription.text = product.description

            if (product.colors.isNullOrEmpty())
                tvProductColors.visibility = View.INVISIBLE
            if (product.sizes.isNullOrEmpty())
                tvProductSize.visibility = View.INVISIBLE
        }

        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let { colorsAdapter.differ.submitList(it) }
        product.sizes?.let { sizesAdapter.differ.submitList(it) }
    }



    private fun setupViewpager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
        }

    }
    private fun setupColorsRv(){
        binding.rvColors.apply {
            adapter=colorsAdapter
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }
    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }


}