package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartActivity : AppCompatActivity() {
    private lateinit var ivBackCart: ImageView
    private lateinit var btnPay: Button
    private lateinit var navbarBott: BottomNavigationView
    //khai bao tang giam so luong sp
    private lateinit var btnMinus: Button
    private lateinit var btnPlus: Button
    private lateinit var tvSolg: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        setControll()
        setEventNavBar()
        setEventBack()
        setEventPay()
        setTangGiamSL()
        navbarBott.menu.findItem(R.id.nav_shoppingcart).isChecked = true
    }
    private fun setControll(){
        //btnBack
        ivBackCart = findViewById<ImageView>(R.id.ivBackCart)
        //nav
        navbarBott = findViewById<BottomNavigationView>(R.id.bottom_navigationCart)
        //btnPay
        btnPay = findViewById<Button>(R.id.btnThanhToan)

        //btn minus
        btnMinus= findViewById<Button>(R.id.btnMinusCart)
        //btn plus
        btnPlus= findViewById<Button>(R.id.btnPlusCart)
        //tv solg
        tvSolg = findViewById<TextView>(R.id.tvSoLg)
    }
    private fun setTangGiamSL() {
        var slInt = tvSolg.text.toString().toIntOrNull() ?: 1

        btnMinus.setOnClickListener {
            if (slInt > 1) { // Không cho phép giá trị < 1
                slInt -= 1
                tvSolg.text = slInt.toString()
            } else {
                Toast.makeText(this, "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show()
            }
        }

        btnPlus.setOnClickListener {
            slInt += 1
            tvSolg.text = slInt.toString()
        }
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
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setEventNavBar(){
        navbarBott.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
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
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}