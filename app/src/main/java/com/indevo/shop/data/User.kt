package com.indevo.shop.data

data class User(
    val firstName:String,
    val lastName:String,
    val email:String,
    var imagePath:String=""
)
    {
       constructor(): this("","","","")
    }

