package com.example.nhom3_project

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var imgLogo: ImageView
    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button
    private lateinit var mAuth: FirebaseAuth

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
        val fadeIn = ObjectAnimator.ofFloat(imgLogo, "alpha", 0f, 1f)
        val scaleX = ObjectAnimator.ofFloat(imgLogo, "scaleX", 0.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(imgLogo, "scaleY", 0.5f, 1f)
        fadeIn.duration = 1000
        scaleX.duration = 1000
        scaleY.duration = 1000

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(fadeIn, scaleX, scaleY)
        imgLogo.visibility = View.VISIBLE
        animatorSet.start()

        mAuth = FirebaseAuth.getInstance()
        lifecycleScope.launch {
            delay(1000)
            mAuth.signOut()
            val user = mAuth.currentUser
            if (user == null) {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            } else {
                val mainIntent = Intent(this@SplashActivity, RegisterActivity::class.java)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(mainIntent)
            }
            finish()
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
