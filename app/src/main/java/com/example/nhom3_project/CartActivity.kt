package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartActivity : AppCompatActivity() {
    private lateinit var ivBackCart: ImageView
    private lateinit var btnPay: Button
    private lateinit var navbarBott: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        setControll()
        setEventNavBar()
        setEventBack()
        setEventPay()
        navbarBott.menu.findItem(R.id.nav_shoppingcart).isChecked = true
    }
    private fun setControll(){
        //btnBack
        ivBackCart = findViewById<ImageView>(R.id.ivBackCart)
        //nav
        navbarBott = findViewById<BottomNavigationView>(R.id.bottom_navigationCart)
        //btnPay
        btnPay = findViewById<Button>(R.id.btnThanhToan)
    }
    private fun setEventPay(){
        btnPay.setOnClickListener{
            // Chuyển
            val intent = Intent(this, PayActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setEventBack(){
        ivBackCart.setOnClickListener{
            // Quay lại hoặc hiện thông báo
            Toast.makeText(this, "Back button clicked", Toast.LENGTH_SHORT).show()
//            finish() // Kết thúc activity để quay lại màn hình trước
            // Chuyển
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setEventNavBar(){
        navbarBott.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Xử lý khi chọn Home
                    true
                }
                R.id.nav_search -> {
                    // Xử lý khi chọn Search
                    true
                }
                R.id.nav_wishlist -> {
                    // Xử lý khi chọn Favorites
                    true
                }
                R.id.nav_account -> {
                    // Xử lý khi chọn Profile
                    true
                }
                else -> false
            }
        }
    }
}