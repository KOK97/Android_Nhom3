package com.example.nhom3_project

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class PayActivity : AppCompatActivity() {

    private lateinit var ivBackPay: ImageView
    private lateinit var btnAccepp: Button
    private lateinit var navbarBott: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pay)
        setControll()
        eventNavBar()
        eventBack()
        navbarBott.menu.findItem(R.id.nav_shoppingcart).isChecked = true

    }
    private fun setControll(){
        //btnBack
        ivBackPay = findViewById<ImageView>(R.id.ivBackPay)
        //nav
        navbarBott = findViewById<BottomNavigationView>(R.id.bottomNavigationViewPay)
        //btnPay
        btnAccepp = findViewById<Button>(R.id.btnAccepp)
    }

    private fun eventBack(){
        ivBackPay.setOnClickListener{
            // Quay lại hoặc hiện thông báo
            Toast.makeText(this, "Back button clicked", Toast.LENGTH_SHORT).show()
            finish() // Kết thúc activity để quay lại màn hình trước
        }
    }
    private fun eventNavBar(){

        navbarBott.setOnNavigationItemSelectedListener{ item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Xử lý khi chọn Home
                    true
                }
                R.id.nav_search -> {
                    // Xử lý khi chọn Search
                    true
                }
                R.id.nav_shoppingcart -> {
                    // Chuyển
//                    val intent = Intent(this, Cart::class.java)
//                    startActivity(intent)
                    true
                }
                R.id.nav_wishlist -> {
                    // Xử lý khi chọn Favorites
                    true
                }
                R.id.nav_wishlist -> {
                    // Xử lý khi chọn Profile
                    true
                }
                else -> false
            }
        }
    }
}