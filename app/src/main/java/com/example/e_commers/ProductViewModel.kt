package com.example.e_commers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Response

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository()
    val productData = MutableLiveData<ProductDetailsResponse>()
    val error = MutableLiveData<String>()

    fun fetchProductDetails() {
        repository.getProductDetails().enqueue(object : retrofit2.Callback<ProductDetailsResponse> {
            override fun onResponse(
                call: Call<ProductDetailsResponse>,
                response: Response<ProductDetailsResponse>
            ) {
                if (response.isSuccessful) {
                    productData.postValue(response.body())
                } else {
                    error.postValue("Failed to load product")
                }
            }

            override fun onFailure(call: Call<ProductDetailsResponse>, t: Throwable) {
                error.postValue(t.localizedMessage ?: "Unknown error")
            }
        })
    }
}

