package com.indevo.shop.utils

import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.indevo.shop.adapter.activities.ShopingActivity

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShopingActivity).findViewById<BottomNavigationView>(
        com.indevo.shop.R.id.bottomNavigation
        )
    bottomNavigationView.visibility = android.view.View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShopingActivity).findViewById<BottomNavigationView>(
            com.indevo.shop.R.id.bottomNavigation
        )
    bottomNavigationView.visibility = android.view.View.VISIBLE
}