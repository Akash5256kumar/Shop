package com.indevo.shop.helpher

import android.health.connect.datatypes.units.Percentage

fun Float?.getProductPrice(price: Float): Float{
    //this --> Percentage
    if (this == null)
        return price
    val remainingPricePercentage = 1f - this
    val priceAfterOffer = remainingPricePercentage * price

    return priceAfterOffer
}