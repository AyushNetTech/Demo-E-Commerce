package com.example.e_commers

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("productdetails/6701/253620?lang=en&store=KWD")
    fun getProductDetails(): Call<ProductDetailsResponse>
}
