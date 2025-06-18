package com.example.e_commers

import retrofit2.Call

class ProductRepository {
    fun getProductDetails(): Call<ProductDetailsResponse> {
        return RetrofitClient.apiService.getProductDetails()
    }
}
