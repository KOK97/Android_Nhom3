package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccountActivity : AppCompatActivity() {
    private lateinit var btnLogout: com.google.android.material.card.MaterialCardView
    private lateinit var navbarBott: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)
        setControl()
        setEvent()
        setEventNavBar()
    }

    private fun setControl() {
        btnLogout = findViewById(R.id.btnLogout)
        //nav
        navbarBott = findViewById<BottomNavigationView>(R.id.bottom_navigation)
    }

    private fun setEvent() {
        btnLogout.setOnClickListener {
            // Hiển thị hộp thoại xác nhận đăng xuất
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Xác nhận")
            builder.setMessage("Bạn có muốn đăng xuất không?")

            // Nút "Đồng ý"
            builder.setPositiveButton("Đồng ý") { _, _ ->
                // Điều hướng về màn hình SplashActivity
                val intent = Intent(this, SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish() // Kết thúc Activity hiện tại
            }

            // Nút "Hủy"
            builder.setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss() // Đóng hộp thoại
            }

            // Hiển thị hộp thoại
            val alertDialog = builder.create()
            alertDialog.show()
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
                    true
                }
                else -> false
            }
        }
    }
}