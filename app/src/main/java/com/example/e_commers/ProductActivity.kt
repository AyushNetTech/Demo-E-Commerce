package com.example.e_commers

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
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

        // Observe product data
        viewModel.productData.observe(this) { product ->
            productTitle.text = product.data.name
            val brandFName = product.data.sku.split("-")
            val firstWord = brandFName.firstOrNull() ?: ""
            productBrandDis.text = product.data.name
            productBrandName.text = firstWord.uppercase(Locale.getDefault())
            productSku.text = "Sku: ${product.data.sku}"
            val num = product.data.price.toDoubleOrNull() ?: 0.0
            productPrice.text = "${"%.2f".format(num)} KWD"
            productDescription.text = product.data.description

            val imageUrl = product.data.image
            Log.d("ProductActivity", "Image URL: $imageUrl")

            Glide.with(this)
                .load(imageUrl)
                .into(productImage)

            // ✅ Load color options
            val attributes = product.data.configurable_option.firstOrNull()?.attributes ?: emptyList()

            val colorOptions = attributes.map {
                ColorOption(
                    value = it.value,
                    swatchUrl = it.swatchUrl
                )
            }

            populateColorOptions(colorOptions)
        }

        // Observe error
        viewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
    private fun populateColorOptions(colors: List<ColorOption>) {
        val container = findViewById<LinearLayout>(R.id.colorContainer)
        container.removeAllViews()

        val size = 140 // Circle size in pixels

        for (color in colors) {
            // FrameLayout: acts as border container
            val frameLayout = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(16, 0, 16, 0)
                }
                background = ContextCompat.getDrawable(this@ProductActivity, R.drawable.color_circle_bg)
            }

            // ImageView inside it, with padding to reveal border
            val imageView = ImageView(this).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
                setPadding(6, 6, 6, 6) // Important: creates gap for border to show
            }

            Glide.with(this)
                .load(color.swatchUrl)
                .circleCrop()
                .into(imageView)

            frameLayout.addView(imageView)
            container.addView(frameLayout)
        }
    }

}
