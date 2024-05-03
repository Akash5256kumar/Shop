package com.indevo.shop.fragment.loginRegister

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.indevo.shop.R
import com.indevo.shop.databinding.FragmentAccountOptionsBinding

class AccountOptionsFragment : Fragment() {
private lateinit var binding: FragmentAccountOptionsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.accountbuttonlogin.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptionsFragment2_to_loginFragment)
        }
binding.acountbuttonregister.setOnClickListener {
    findNavController().navigate(R.id.action_accountOptionsFragment2_to_registerFragment)
}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding= FragmentAccountOptionsBinding.inflate(inflater)
        return binding.root
    }


}