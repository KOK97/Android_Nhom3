package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AccountActivity : AppCompatActivity() {
    private lateinit var btnLogout: com.google.android.material.card.MaterialCardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)
        setControl()
        setEvent()
    }

    private fun setControl() {
        btnLogout = findViewById(R.id.btnLogout)
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

}