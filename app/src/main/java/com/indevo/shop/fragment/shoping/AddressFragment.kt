package com.indevo.shop.fragment.shoping

import android.location.Address
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.R
import com.indevo.shop.databinding.FragmentAdressBinding

import com.indevo.shop.utils.Resource
import com.indevo.shop.viewmodel.AddressViewModel
import com.indevo.shop.viewmodel.CartViewModel
import com.indevo.shop.viewmodelfactory.AdressViewModelFactory
import com.indevo.shop.viewmodelfactory.CartViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class AddressFragment : Fragment() {
    private lateinit var binding: FragmentAdressBinding
  private lateinit var viewModel:AddressViewModel
    val args by navArgs<AddressFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAdressBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, AdressViewModelFactory(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())).get(AddressViewModel::class.java)

        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Sucess -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        val address = args.address
//        if (address == null) {
//            binding.buttonDelelte.visibility = View.GONE
//        } else {
//            binding.apply {
//                edAddressTitle.setText(address.addressTitle)
//                edFullName.setText(address.fullName)
//                edState.setText(address.street)
//                edPhone.setText(address.phone)
//                edCity.setText(address.city)
//                edState.setText(address.state)
//            }
//        }

        binding.apply {
            buttonSave.setOnClickListener {
                val addressTitle = edAddressTitle.text.toString()
                val fullName = edFullName.text.toString()
                val street = edStreet.text.toString()
                val phone = edPhone.text.toString()
                val city = edCity.text.toString()
                val state = edState.text.toString()
                val address = com.indevo.shop.data.Address(addressTitle, fullName, street, phone, city, state)

                viewModel.addAddress(address)
            }
        }


    }

}