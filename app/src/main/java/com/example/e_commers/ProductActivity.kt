package com.example.e_commers

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
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
    private lateinit var discriptionArrow: ImageView
    private lateinit var scrollV: ScrollView

    private var isDescriptionVisible = false

    private val viewModel: ProductViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        window.statusBarColor = ContextCompat.getColor(this, R.color.OffWhite)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true


        productImage = findViewById(R.id.productImage)
        productTitle = findViewById(R.id.Title)
        productSku = findViewById(R.id.productSku)
        productPrice = findViewById(R.id.productPrice)
        productBrandName = findViewById(R.id.productTitle)
        productBrandDis = findViewById(R.id.productSubTitle)
        productDescription = findViewById(R.id.productDescription)
        discriptionArrow = findViewById(R.id.disArrow)
        scrollV= findViewById(R.id.scrolling)

        productDescription.visibility = View.GONE

        discriptionArrow.setOnClickListener {
            isDescriptionVisible = !isDescriptionVisible

            if (isDescriptionVisible) {
                productDescription.visibility = View.VISIBLE
                discriptionArrow.setImageResource(R.drawable.arrowup)
                scrollV.post {
                    scrollV.smoothScrollTo(1, scrollV.bottom)
                }
            } else {
                productDescription.visibility = View.GONE
                discriptionArrow.setImageResource(R.drawable.arrowdown)
            }
        }

        viewModel.fetchProductDetails()

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

            val attributes = product.data.configurable_option.firstOrNull()?.attributes ?: emptyList()

            val colorOptions = attributes.map {
                ColorOption(
                    value = it.value,
                    swatchUrl = it.swatchUrl
                )
            }

            populateColorOptions(colorOptions)
        }

        viewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
    private fun populateColorOptions(colors: List<ColorOption>) {
        val container = findViewById<LinearLayout>(R.id.colorContainer)
        container.removeAllViews()

        val size = 140

        for (color in colors) {

            val frameLayout = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(15, 0, 15, 0)
                }
                background = ContextCompat.getDrawable(this@ProductActivity, R.drawable.color_circle_bg)
            }

            val imageView = ImageView(this).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
                setPadding(6, 6, 6, 6)
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
