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
    val stock_status: String,
    val configurable_option: List<ConfigurableOption>
)

data class Category(
    val id: String,
    val name: String
)

data class Rating(
    val value: Int,
    val count: Int
)

data class ColorOption(
    val value: String,
    val swatchUrl: String,
    val previewImageUrl: String,
    val previewImageList: List<String> // New field
)


data class ConfigurableOption(
    @SerializedName("attribute_id")
    val attributeId: Int,

    val type: String,

    @SerializedName("attribute_code")
    val attributeCode: String,

    val attributes: List<ColorAttribute>
)

data class ColorAttribute(
    val value: String,

    @SerializedName("swatch_url")
    val swatchUrl: String,

    val images: List<String>
)




