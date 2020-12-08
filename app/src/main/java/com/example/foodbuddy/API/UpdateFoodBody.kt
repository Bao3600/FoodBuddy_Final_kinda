package com.example.foodbud

data class UpdateFoodBody(val item: String,
                          val foodAmt: String,
                          val foodUt: String,
                          val expDate: String,
                          val token: String)