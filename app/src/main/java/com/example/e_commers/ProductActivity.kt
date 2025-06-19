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
    private lateinit var productSku: TextView
    private lateinit var productDescription: TextView

    private val viewModel: ProductViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        productImage = findViewById(R.id.productImage)
        productTitle = findViewById(R.id.Title)
        productSku = findViewById(R.id.productSku)
        productPrice = findViewById(R.id.productPrice)
        productBrandName = findViewById(R.id.productTitle)
        productBrandDis = findViewById(R.id.productSubTitle)

        productDescription = findViewById(R.id.productDescription)

        viewModel.fetchProductDetails()

        viewModel.productData.observe(this) { product ->
            productTitle.text = product.data.name
            val BrandFName=product.data.sku.split("-")
            val firstWord = BrandFName.firstOrNull() ?: ""
            productBrandDis.text = product.data.name
            productBrandName.text= firstWord.uppercase()
            productSku.text = "Sku: ${product.data.sku}"
            val num=product.data.price.toDouble()
            productPrice.text = "${"%.2f".format(num)} KWD"
            productDescription.text = product.data.description

            val imageUrl = product.data.image

            Log.d("ProductActivity", "Image URL: ${imageUrl}")
            Log.d("ProductActivity", "kwd: ${"%.2f".format(num)}")

            Glide.with(this)
                .load(imageUrl)
                .into(productImage)
        }

        viewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}
