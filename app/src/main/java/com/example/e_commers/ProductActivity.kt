package com.example.e_commers

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.capitalize
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.merge
import java.util.Locale

class ProductActivity : AppCompatActivity() {

    private lateinit var productImage: ImageView
    private lateinit var productTitle: TextView
    private lateinit var productBrandName: TextView
    private lateinit var productPrice: TextView
    private lateinit var productBrandDis: TextView
    private lateinit var productDescription: TextView

    private val viewModel: ProductViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        productImage = findViewById(R.id.productImage)
        productTitle = findViewById(R.id.productTitle)
        productPrice = findViewById(R.id.productPrice)
        productBrandName = findViewById(R.id.brandName)
        productBrandDis = findViewById(R.id.brandDis)

        productDescription = findViewById(R.id.productDescription)

        viewModel.fetchProductDetails()

        viewModel.productData.observe(this) { product ->
            productTitle.text = product.data.name
            val BrandFName=product.data.sku.split("-")
            val firstWord = BrandFName.firstOrNull() ?: ""
            productBrandDis.text = product.data.name
            productBrandName.text= firstWord.uppercase()
            productPrice.text = "${product.data.price} KWD"
            productDescription.text = product.data.description

            val imageUrl = product.data.image

            Log.d("ProductActivity", "Image URL: ${imageUrl}")
            Log.d("ProductActivity", "Name: ${product.data.name}")

            Glide.with(this)
                .load(imageUrl)
                .into(productImage)
        }

        viewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}
