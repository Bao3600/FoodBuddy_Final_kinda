package com.example.foodbuddy.API

data class AddFoodBody(val item: String,
                       val foodAmt: String,
                       val foodUt: String,
                       val expDate: String,
                       val token: String)