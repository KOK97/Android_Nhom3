package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, HomeActivity::class.java)
        val splashScreen = installSplashScreen()

        startActivity(intent)
        splashScreen.setKeepOnScreenCondition{true}
        addLoading(splashScreen)
    }
    private fun addLoading(splashScreen: SplashScreen){
        Handler(Looper.getMainLooper()).postDelayed({
            splashScreen.setKeepOnScreenCondition{false}
        },2000)
    }
}