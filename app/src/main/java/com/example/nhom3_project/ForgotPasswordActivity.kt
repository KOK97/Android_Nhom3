package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.*

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var btnReset: Button
    private lateinit var edtEmail: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var strEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        mAuth = FirebaseAuth.getInstance()
        setControl()
        setEvent()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
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

            if (TextUtils.isEmpty(strEmail)) {
                edtEmail.error = "Vui lòng nhập email!"
                edtEmail.requestFocus()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                edtEmail.error = "Email không hợp lệ!"
                edtEmail.requestFocus()
            } else {
                checkEmailAndSendReset(strEmail)
            }
        }
    }

    private fun checkEmailAndSendReset(email: String) {
        progressBar.visibility = View.VISIBLE
        btnReset.isEnabled = false

        val databaseRef = FirebaseDatabase.getInstance().getReference("Users")
         databaseRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        sendPasswordResetEmail(email)
                    } else {
                        progressBar.visibility = View.GONE
                        btnReset.isEnabled = true
                        edtEmail.error = "Email không tồn tại trong hệ thống!"
                        edtEmail.requestFocus()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    progressBar.visibility = View.GONE
                    btnReset.isEnabled = true
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Đã xảy ra lỗi: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun sendPasswordResetEmail(email: String) {
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                progressBar.visibility = View.GONE
                btnReset.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Đã gửi đường dẫn đặt lại mật khẩu cho email của bạn đã đăng ký.",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    handleError(task.exception)
                }
            }
    }

    private fun handleError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidUserException -> "Email không tồn tại trong hệ thống."
            is FirebaseAuthInvalidCredentialsException -> "Email không hợp lệ. Vui lòng kiểm tra lại"
            else -> exception?.message ?: "Đã xảy ra lỗi không xác định."
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        progressBar.visibility = View.GONE
        btnReset.isEnabled = true
    }
}
