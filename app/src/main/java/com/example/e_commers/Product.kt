package com.example.e_commers

import com.google.gson.annotations.SerializedName

data class ProductDetailsResponse(
    val success: Boolean,
    val data: ProductData
)

data class ProductData(
    val id: Int,
    val sku: String,
    val name: String,
    val price: String,
    val description: String,
    val short_description: String,
    val image: String,
    val gallery: List<String>,
    val brand: String,
    val category: List<Category>,
    val size: String,
    val gender: String,
    val rating: Rating,
    val is_favorite: Boolean,
    val stock_status: String
)

data class Category(
    val id: String,
    val name: String
)

data class Rating(
    val value: Int,
    val count: Int
)

