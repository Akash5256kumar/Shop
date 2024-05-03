package com.indevo.shop.adapter.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.indevo.shop.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//@AndroidEntryPoint

class LoginRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()




    }
}