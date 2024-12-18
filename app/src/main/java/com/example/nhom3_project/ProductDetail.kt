package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductDetail : AppCompatActivity() {
    private lateinit var tvNameDetail: TextView
    private lateinit var tvPriceDetail: TextView
    private lateinit var tvIdDetait: TextView
    private lateinit var ivProDetail: ImageView

    private lateinit var btnExit :Button
    private lateinit var btnAddCart :Button
    private lateinit var btnBuyNow :Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)
        setControl()
        setEvent()
    }
    private  fun setControl(){
        btnExit = findViewById(R.id.btnExit)
        btnAddCart = findViewById(R.id.btnAddCart)
        btnBuyNow = findViewById(R.id.btnBuyNow)

        tvNameDetail = findViewById(R.id.tvNameDetail)
        tvPriceDetail = findViewById(R.id.tvPriceDetail)
        tvIdDetait = findViewById(R.id.tvIdDetail)
        ivProDetail = findViewById(R.id.ivProDetail)
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
                        val productPrice = snapshot.child("price").getValue(Int::class.java) ?: 0
                        val imageUrl = snapshot.child("imageUrl").getValue(String::class.java)?:"No image"
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
    }
}