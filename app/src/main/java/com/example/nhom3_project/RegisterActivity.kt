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
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class RegisterActivity : AppCompatActivity() {

    private lateinit var txtExistaccount: TextView
    private lateinit var btnRegister: Button
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var mAuth: FirebaseAuth
    private lateinit var edtUserName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtPassword: EditText
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
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
        txtExistaccount = findViewById(R.id.txtExistaccount)
        btnRegister = findViewById(R.id.btnRegister)
        toolbar = findViewById(R.id.toolbar)
        edtUserName = findViewById(R.id.edtUserName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhone = findViewById(R.id.edtPhone)
        edtPassword = findViewById(R.id.edtPassword)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setEvent() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Quay lại"
            setDisplayHomeAsUpEnabled(true)
        }

        txtExistaccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val username = edtUserName.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val phone = edtPhone.text.toString().trim()

            // Kiểm tra dữ liệu đầu vào
            when {
                username.isEmpty() -> {
                    edtUserName.error = "Tên người dùng không được để trống"
                    edtUserName.requestFocus()
                }
                !isValidUsername(username) -> {
                    edtUserName.error = "Tên người dùng không được chứa ký tự đặc biệt"
                    edtUserName.requestFocus()
                }
                email.isEmpty() -> {
                    edtEmail.error = "Email không được để trống"
                    edtEmail.requestFocus()
                }
                !email.isValidEmail() -> {
                    edtEmail.error = "Email không hợp lệ"
                    edtEmail.requestFocus()
                }
                password.length < 6 -> {
                    edtPassword.error = "Mật khẩu phải có ít nhất 6 ký tự"
                    edtPassword.requestFocus()
                }
                phone.isEmpty() -> {
                    edtPhone.error = "Số điện thoại không được để trống"
                    edtPhone.requestFocus()
                }
                phone.length < 6 -> {
                    edtPhone.error = "Số điện thoại có ít nhất 6 số"
                    edtPhone.requestFocus()
                }
                else -> {
                    registerUser(email, password, username, phone)
                }
            }
        }
    }

    private fun registerUser(email: String, password: String, username: String, phone: String) {
        progressBar.visibility = View.VISIBLE
        btnRegister.isEnabled = false
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            progressBar.visibility = View.GONE
            btnRegister.isEnabled = true
            if (task.isSuccessful) {
                val user = mAuth.currentUser
                user?.sendEmailVerification()?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        saveUserToDatabase(user.uid, username, email, phone)
                        Toast.makeText(
                            this, "Đăng ký thành công! Vui lòng xác minh email.", Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this, "Không thể gửi email xác minh.", Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                handleRegistrationError(task.exception)
            }
        }
    }

    private fun saveUserToDatabase(uid: String, username: String, email: String, phone: String) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Users")
        val userMap = hashMapOf(
            "uid" to uid,
            "name" to username,
            "email" to email,
            "phone" to phone,
            "role" to "Customer",
            "typingTo" to "noOne"
        )
        reference.child(uid).setValue(userMap)
    }

    private fun handleRegistrationError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidCredentialsException -> "Email không hợp lệ."
            is FirebaseAuthUserCollisionException -> "Email đã được sử dụng. Vui lòng chọn email khác."
            else -> exception?.message ?: "Đã xảy ra lỗi không xác định."
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun isValidUsername(username: String): Boolean {
        val regex = "^[A-Za-z0-9_]+$"
        return username.matches(regex.toRegex())
    }

}
