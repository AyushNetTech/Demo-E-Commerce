package com.example.e_commers

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.View
import android.view.ViewGroup
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
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Locale

class ProductActivity : AppCompatActivity() {

    private lateinit var productTitle: TextView
    private lateinit var productBrandName: TextView
    private lateinit var productPrice: TextView
    private lateinit var productBrandDis: TextView
    private lateinit var productSku: TextView
    private lateinit var productDescription: TextView
    private lateinit var discriptionArrow: ImageView
    private lateinit var scrollV: ScrollView

    private var isDescriptionVisible = false
    private var selectedSwatchIndex = -1
    private lateinit var galleryContainer: LinearLayout

    private lateinit var imageSlider: ViewPager2
    private lateinit var dotIndicator: TabLayout


    private val viewModel: ProductViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        window.statusBarColor = ContextCompat.getColor(this, R.color.OffWhite)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        galleryContainer = findViewById(R.id.galleryContainer)


        imageSlider = findViewById(R.id.imageSlider)
        dotIndicator = findViewById(R.id.dotIndicator)

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
            productSku.text = "SKU: ${product.data.sku}"
            val num = product.data.price.toDoubleOrNull() ?: 0.0
            productPrice.text = "${"%.2f".format(num)} KWD"
            val rawHtml = product.data.description
            val formattedText = formatHtmlDescription(rawHtml)
            productDescription.text = formattedText

            val imageUrl = product.data.image
            Log.d("ProductActivity", "Image URL: $imageUrl")

            val attributes = product.data.configurable_option.firstOrNull()?.attributes ?: emptyList()

            val colorOptions = attributes.map {
                ColorOption(
                    value = it.value,
                    swatchUrl = it.swatchUrl,
                    previewImageUrl = it.images.firstOrNull() ?: "",
                    previewImageList = it.images
                )
            }

            if (colorOptions.isNotEmpty()) {
                selectedSwatchIndex = 0
                showGalleryImages(colorOptions[0].previewImageList)
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

        colors.forEachIndexed { index, color ->
            val frameLayout = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(15, 0, 15, 0)
                }

                background = ContextCompat.getDrawable(
                    this@ProductActivity,
                    if (index == selectedSwatchIndex) R.drawable.color_circle_bg else R.drawable.color_circle_bg
                )
            }

            val imageView = ImageView(this).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
                setPadding(6, 6, 6, 6)

                setOnClickListener {
                    selectedSwatchIndex = index
                    populateColorOptions(colors)
                    showGalleryImages(color.previewImageList)
                }
            }

            Glide.with(this)
                .load(color.swatchUrl)
                .circleCrop()
                .into(imageView)

            frameLayout.addView(imageView)
            container.addView(frameLayout)
        }
    }


    @Suppress("DEPRECATION")
    fun formatHtmlDescription(rawHtml: String): Spanned {
        // Clean and format HTML string
        val cleanedHtml = rawHtml
            .replace("\r\n", "")
            .replace("<ul>", "")
            .replace("</ul>", "")
            .replace("<li>", "• ")
            .replace("</li>", "<br>")
            .replace("<br>", "<br/>")
            .replace(Regex("(?i)<p[^>]*>"), "")
            .replace("</p>", "<br/>")
            .replace("&nbsp;", " ")
            .replace(Regex("<(?!br|b|/b)[^>]+>"), "")
            .replace(Regex("(?i)(Key Features:|How To Wear Your Contact Lense:|About Anesthesia:)")) {
                "<b>${it.value}</b>"
            }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(cleanedHtml, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(cleanedHtml)
        }
    }

    private fun showGalleryImages(images: List<String>) {
        val adapter = ImageSliderAdapter(images)
        imageSlider.adapter = adapter

        TabLayoutMediator(dotIndicator, imageSlider) { _, _ -> }.attach()

        val tabStrip = dotIndicator.getChildAt(0) as? ViewGroup
        tabStrip?.let {
            for (i in 0 until it.childCount) {
                val tab = it.getChildAt(i)
                val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(6, 0, 6, 0)
                tab.layoutParams = layoutParams
            }
        }

    }



}
