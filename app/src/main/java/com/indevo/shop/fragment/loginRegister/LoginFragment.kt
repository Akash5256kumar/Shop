package com.indevo.shop.fragment.loginRegister

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.indevo.shop.R
import com.indevo.shop.adapter.activities.ShopingActivity
import com.indevo.shop.databinding.FragmentLoginBinding
import com.indevo.shop.dilog.setupBottomSheetDialog
import com.indevo.shop.utils.Resource
import com.indevo.shop.viewmodelfactory.LoginViewMOdelFactory
import com.indevo.shop.viewmodel.LoginViewModel

class LoginFragment : Fragment() {
    private lateinit var binding:FragmentLoginBinding
    private lateinit var viewmodel:LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.donothavehaveaccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        viewmodel = ViewModelProvider(this, LoginViewMOdelFactory(FirebaseAuth.getInstance())).get(LoginViewModel::class.java)

        binding.apply {
            buttonlogin.setOnClickListener {
                val email = edtemail.text.toString().trim()
                val password = edtpassword.text.toString()
                viewmodel.login(email, password)
            }
        }



        lifecycleScope.launchWhenStarted {
            viewmodel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonlogin.startAnimation()
                    }
                    is Resource.Sucess -> {
                        binding.buttonlogin.revertAnimation()
                        Intent(requireActivity(), ShopingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.buttonlogin.revertAnimation()
                    }
                    else -> Unit

                }
            }
        }


        binding.forgatepaasword.setOnClickListener {
            setupBottomSheetDialog { email ->
                viewmodel.resetPassword(email)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewmodel.resetPassword.collect{
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Sucess -> {
                        Snackbar.make(requireView(),"Reset link was sent to your email", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(),"Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit

                }
            }
        }
    }

}

