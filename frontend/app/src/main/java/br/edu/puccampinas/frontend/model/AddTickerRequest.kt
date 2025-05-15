package br.edu.puccampinas.frontend.model

import com.google.gson.annotations.SerializedName

data class AddTickerRequest(
    val username: String,
    val ticker: String,
    @SerializedName("purchase_quantity")
    val purchaseQuantity: Int
)
