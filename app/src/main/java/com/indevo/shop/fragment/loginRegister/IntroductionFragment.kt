package com.indevo.shop.fragment.loginRegister

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.indevo.shop.R
import com.indevo.shop.adapter.activities.ShopingActivity
import com.indevo.shop.databinding.FragmentIntroductionBinding
import com.indevo.shop.viewmodel.IntroductionViewModel
import com.indevo.shop.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.indevo.shop.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import com.indevo.shop.viewmodel.LoginViewModel
import com.indevo.shop.viewmodelfactory.IntroductionViewModelFactory
import com.indevo.shop.viewmodelfactory.LoginViewMOdelFactory


class IntroductionFragment : Fragment() {
    private lateinit var binding:FragmentIntroductionBinding
    private lateinit var viewModel:IntroductionViewModel
    


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            IntroductionViewModelFactory(
                sharedPreferences = requireActivity().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE),
                firebaseAuth = FirebaseAuth.getInstance()
            )
        ).get(IntroductionViewModel::class.java)

        lifecycleScope.launchWhenStarted {
            viewModel.navigate.collect {
                when (it) {
                    SHOPPING_ACTIVITY -> {
                        Intent(requireActivity(), ShopingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    ACCOUNT_OPTIONS_FRAGMENT -> {
                        findNavController().navigate(it)
                    }

                    else -> Unit
                }
            }
        }
        binding.buttonStart.setOnClickListener {
            viewModel.startButtonClick()
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment2)
        }

        }




}