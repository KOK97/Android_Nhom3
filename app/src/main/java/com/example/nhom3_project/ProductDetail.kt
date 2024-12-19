package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductDetail : AppCompatActivity() {
    private lateinit var tvNameDetail: TextView
    private lateinit var tvPriceDetail: TextView
    private lateinit var tvIdDetait: TextView
    private lateinit var tvType: TextView
    private lateinit var tvDesc: TextView
    private lateinit var tvRating: TextView
    private lateinit var ivProDetail: ImageView
    private lateinit var ratingBar : RatingBar
    private lateinit var tvTotalCMT : TextView
    private lateinit var tvBoxCMT : TextView

    private lateinit var btnExit :Button
    private lateinit var btnAddCart :Button
    private lateinit var btnBuyNow :Button
    private lateinit var btnComment :Button


    //nav
    private lateinit var navbarBott: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)
        setControl()
        setEvent()
        setEventNavBar()
    }
    private  fun setControl(){
        btnExit = findViewById(R.id.btnExit)
        btnAddCart = findViewById(R.id.btnAddCart)
        btnBuyNow = findViewById(R.id.btnBuyNow)
        btnComment = findViewById(R.id.btnComment)

        tvNameDetail = findViewById(R.id.tvNameDetail)
        tvPriceDetail = findViewById(R.id.tvPriceDetail)
        tvIdDetait = findViewById(R.id.tvIdDetail)
        tvType = findViewById(R.id.tvType)
        tvDesc = findViewById(R.id.tvDesc)
        ivProDetail = findViewById(R.id.ivProDetail)

        tvRating = findViewById(R.id.tvRating)
        ratingBar = findViewById(R.id.ratingBar)

        tvTotalCMT = findViewById(R.id.tvTotalCmt)
        tvBoxCMT = findViewById(R.id.tvBoxCmt)

        //nav
        navbarBott = findViewById<BottomNavigationView>(R.id.bottom_navigationCart)

    }
    private  fun setEvent(){
        val getProductID = intent.getStringExtra("productClick")
        Toast.makeText(this, "Clicked on: $getProductID", Toast.LENGTH_SHORT).show()
        val databaseReference = FirebaseDatabase.getInstance().getReference("products")
        val productId = getProductID

        if (productId != null) {
            databaseReference.child(productId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Retrieve product details from the snapshot
                        val productName = snapshot.child("name").getValue(String::class.java) ?: "No Name"
                        val productDesc = snapshot.child("desc").getValue(String::class.java) ?: "No Desc"
                        val productType = snapshot.child("type").getValue(String::class.java) ?: "No Type"
                        val productPrice = snapshot.child("price").getValue(Int::class.java) ?: 0
                        val imageUrl2 = snapshot.child("imageUrl").getValue(String::class.java) ?: "No image"
                        //set data sp2

                        //lay anh tu drawble
                        val imageResId2 = resources.getIdentifier(imageUrl2, "drawable", packageName)

                        if (imageResId2 != 0) {
                            Glide.with(this@ProductDetail)
                                .load(imageResId2)
                                .override(400, 150)
                                .into(ivProDetail)
                        }
                        else{
                            //lay anh truc tiep tu url
                            Glide.with(this@ProductDetail)
                                .load("$imageUrl2")
                                .override(400, 150)
                                .into(ivProDetail)
                        }
                        tvNameDetail.text = productName
                        tvPriceDetail.text = productPrice.toString()
                        tvIdDetait.text = productId
                        tvType.text = productType
                        tvDesc.text = productDesc
                        tvTotalCMT.text = "Tổng cộng 0 đánh giá từ khách hàng"

                        val text = SpannableString("\u200B")

                        // Tải hình ảnh
                        val drawable = ContextCompat.getDrawable(this@ProductDetail, R.drawable.nocmt)
                        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

                        // Chèn hình ảnh vào văn bản
                        val imageSpan = ImageSpan(drawable!!)
                        text.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                        tvBoxCMT.text = text
                    } else {
                        // Handle case when the product doesn't exist
                        Log.e("Firebase", "Product not found")
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Failed to read data", error.toException())
                }

            })
        }


        btnExit.setOnClickListener(){
            finish()
        }
        btnComment.setOnClickListener()
        {
            if(productId != null){
            val intent = Intent(this@ProductDetail, Comment::class.java)
            intent.putExtra("productId", productId)
            startActivity(intent)
            }
        }
        btnAddCart.setOnClickListener(){
            if(productId != null){
                val intent = Intent(this@ProductDetail, CartActivity::class.java)
                intent.putExtra("productId", productId)
                startActivity(intent)
            }
        }
        btnBuyNow.setOnClickListener(){
            if(productId != null){
                val intent = Intent(this@ProductDetail, CartActivity::class.java)
                intent.putExtra("productId", productId)
                startActivity(intent)
            }
        }

        //ratingbar
        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            tvRating.text = rating.toString()
            when(ratingBar.rating.toInt()){
                1 -> tvRating.text = "1"
                2 -> tvRating.text = "2"
                3 -> tvRating.text = "3"
                4 -> tvRating.text = "4"
                5 -> tvRating.text = "5"
                else ->  tvRating.text = "0"
            }
        }
    }
    //nav
    private fun setEventNavBar() {
        navbarBott.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_search -> {
                    // Xử lý khi chọn Search
                    true
                }

                R.id.nav_shoppingcart -> {
                    // Chuyển
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)

                    true
                }

                R.id.nav_wishlist -> {
                    // Xử lý khi chọn Favorites
                    true
                }

                R.id.nav_account -> {
                    // Xử lý khi chọn Profile
                    // Chuyển
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }
}