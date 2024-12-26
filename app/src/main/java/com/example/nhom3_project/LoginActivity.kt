package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginActivity : AppCompatActivity() {
    private lateinit var txtRegister: TextView
    private lateinit var txtForgotPassword: TextView
    private lateinit var btnLogin: Button
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var mAuth: FirebaseAuth
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
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
        txtRegister = findViewById(R.id.txtRegister)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        btnLogin = findViewById(R.id.btnLogin)
        toolbar = findViewById(R.id.toolbar)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setEvent() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Quay lại"
            setDisplayHomeAsUpEnabled(true)
        }
        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            when {
                email.isEmpty() -> {
                    edtEmail.error = "Vui lòng nhập email!"
                    edtEmail.requestFocus()
                }

                !email.isValidEmail() -> {
                    edtEmail.error = "Email không hợp lệ!"
                    edtEmail.requestFocus()
                }

                password.isEmpty() -> {
                    edtPassword.error = "Vui lòng nhập mật khẩu!"
                    edtPassword.requestFocus()
                }

                password.length < 6 -> {
                    edtPassword.error = "Mật khẩu phải có ít nhất 6 ký tự!"
                    edtPassword.requestFocus()
                }

                else -> {
                    loginUser(email, password)
                }
            }
        }

        txtRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        txtForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        btnLogin.isEnabled = false
        edtEmail.isEnabled = false
        edtPassword.isEnabled = false

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            progressBar.visibility = View.GONE
            btnLogin.isEnabled = true
            edtEmail.isEnabled = true
            edtPassword.isEnabled = true
            if (task.isSuccessful) {
                val user = mAuth.currentUser
                if (user != null) {
                    Toast.makeText(
                        this, "Đăng nhập thành công!", Toast.LENGTH_LONG
                    ).show()
                    val mainIntent = Intent(this, HomeActivity::class.java)
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(mainIntent)
                }
            } else {
                handleLoginError(task.exception)
            }
        }
    }

    private fun handleLoginError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidUserException -> "Tài khoản không tồn tại. Vui lòng kiểm tra lại."
            is FirebaseAuthInvalidCredentialsException -> "Email hoặc mật khẩu không đúng."
            else -> exception?.message ?: "Đã xảy ra lỗi không xác định."
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        progressBar.visibility = View.GONE
        btnLogin.isEnabled = true
        edtEmail.isEnabled = true
        edtPassword.isEnabled = true
    }

    private fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun createTestAccounts() {
        val database = FirebaseDatabase.getInstance().getReference("Users")

        for (i in 1..9) {
            val email = "test$i@gmail.com"
            val password = "test123"
            val username = "test$i"
            val phone = "123456789$i"

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        val uid = user?.uid ?: return@addOnCompleteListener
                        val hashMap = mapOf(
                            "uid" to uid,
                            "name" to username,
                            "email" to email,
                            "phone" to phone,
                            "role" to "Customer",
                            "typingTo" to "noOne"
                        )
                        database.child(uid).setValue(hashMap)
                    }
                }
        }
    }
}