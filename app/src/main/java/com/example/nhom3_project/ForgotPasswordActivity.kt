package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var btnReset: Button
    private lateinit var edtEmail: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var strEmail: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
        mAuth = FirebaseAuth.getInstance()
        setControl()
        setEvent()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setControl() {
        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progressBar)
        btnReset = findViewById(R.id.btnReset)
        edtEmail = findViewById(R.id.edtEmail)
    }

    private fun setEvent() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Quay lại"
            setDisplayHomeAsUpEnabled(true)
        }

        btnReset.setOnClickListener {
            strEmail = edtEmail.text.toString().trim()
            if (!TextUtils.isEmpty(strEmail)) {
                ResetPassword()
            } else {
                edtEmail.setError("Vui lòng nhập email !")
                edtEmail.requestFocus()
            }
        }
    }

    private fun ResetPassword() {
        progressBar.visibility = View.VISIBLE
        btnReset.isEnabled = false
        mAuth.sendPasswordResetEmail(strEmail).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressBar.visibility = View.GONE
                btnReset.isEnabled = true
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Đã gửi đường dẫn thay đổi password word email mà bạn đã đăng ký !!",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Gửi không thành công !", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            progressBar.visibility = View.GONE
            btnReset.isEnabled = true
            Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
        }
    }
}