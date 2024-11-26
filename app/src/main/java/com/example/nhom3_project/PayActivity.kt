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

class PayActivity : AppCompatActivity() {

    private lateinit var ivBackPay: ImageView
    private lateinit var btnAccepp: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pay)
        setControll()
        eventBack()
        evenPay()
    }
    private fun setControll(){
        //btnBack
        ivBackPay = findViewById<ImageView>(R.id.ivBackPay)
        //btnPay
        btnAccepp = findViewById<Button>(R.id.btnAccepp)
    }
    private fun evenPay(){
        btnAccepp.setOnClickListener{
            showPaymentSuccessToast()
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }
    private fun eventBack(){
        ivBackPay.setOnClickListener{
            // Quay lại hoặc hiện thông báo
            Toast.makeText(this, "Back button clicked", Toast.LENGTH_SHORT).show()
            finish() // Kết thúc activity để quay lại màn hình trước
        }
    }
    private fun showPaymentSuccessToast() {
        Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
    }
}