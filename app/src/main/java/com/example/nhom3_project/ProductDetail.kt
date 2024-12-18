package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProductDetail : AppCompatActivity() {
    private lateinit var btnExit :Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)
        val productId = intent.getStringExtra("productClick")
        Toast.makeText(this, "Clicked on: $productId", Toast.LENGTH_SHORT).show()
    setControl()
        setEvent()
    }
    private  fun setControl(){
        btnExit = findViewById(R.id.btnExit)
    }
    private  fun setEvent(){
        btnExit.setOnClickListener(){
            finish()
        }
    }
}