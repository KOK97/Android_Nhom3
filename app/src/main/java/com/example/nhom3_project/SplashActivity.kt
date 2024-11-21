package com.example.nhom3_project

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var imgLogo: ImageView
    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setControl()
        setEvent()
    }

    private fun setControl() {
        imgLogo = findViewById(R.id.imgLogo)
        btnRegister = findViewById(R.id.btnRegister)
        btnLogin = findViewById(R.id.btnLogin)
    }

    private fun setEvent() {
        // Tạo hoạt ảnh mờ dần (alpha) cho logo
        val fadeIn = ObjectAnimator.ofFloat(imgLogo, "alpha", 0f, 1f)
        fadeIn.duration = 1000  // 1 giây

        // Tạo hoạt ảnh phóng to (scale) cho logo
        val scaleX = ObjectAnimator.ofFloat(imgLogo, "scaleX", 0.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(imgLogo, "scaleY", 0.5f, 1f)
        scaleX.duration = 1000  // 1 giây
        scaleY.duration = 1000  // 1 giây

        // Kết hợp hai hoạt ảnh trong AnimatorSet để chạy cùng lúc
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(fadeIn, scaleX, scaleY)
        animatorSet.start()
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}