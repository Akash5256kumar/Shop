package com.indevo.shop.fragment.loginRegister
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.R
import com.indevo.shop.data.User
import com.indevo.shop.databinding.FragmentRegisterBinding
import com.indevo.shop.utils.Resource
import com.indevo.shop.viewmodel.RegisterViewModel
import com.indevo.shop.viewmodelfactory.ViewModelFactory

//@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.doyouhaveaccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        viewModel = ViewModelProvider(this, ViewModelFactory( FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())).get(RegisterViewModel::class.java)


        binding.apply {
            buttonRegister.setOnClickListener {
                val user = User(
                    edtfirstName.text.toString().trim(),
                    edtlastName.text.toString().trim(),
                    edtemail.text.toString().trim()
                )
               val password = edtpassword.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonRegister.startAnimation()
                    }
                    is Resource.Sucess -> {
                        Log.d("sucess", it.data.toString())
                    }
                    is Resource.Error -> {
                        Log.e("error", it.message.toString())
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }
}

