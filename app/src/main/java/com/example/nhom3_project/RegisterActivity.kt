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
            val emailText = edtEmail.text.toString().trim()
            val username = edtUserName.text.toString().trim()
            val pass = edtPassword.text.toString().trim()
            val phone = edtPhone.text.toString().trim()

            // Kiểm tra email, mật khẩu và tên người dùng
            when {
                username.isEmpty() -> {
                    edtUserName.error = "Tên người dùng không được để trống"
                    edtUserName.requestFocus()
                }

                !isValidUsername(username) -> {
                    edtUserName.error = "Tên người dùng không được có ký tự đặc biệt"
                    edtUserName.requestFocus()
                }

                !emailText.isValidEmail() -> {
                    edtEmail.error = "Email không hợp lệ"
                    edtEmail.requestFocus()
                }

                pass.length < 6 -> {
                    edtPassword.error = "Mật khẩu phải dài hơn 6 ký tự"
                    edtPassword.requestFocus()
                }

                phone.isEmpty() -> {
                    edtPhone.error = "Số điện thoại không được để trống"
                    edtPhone.requestFocus()
                }

                phone.length < 6 -> {
                    edtPhone.error = "Số điện thoại phải có ít nhất 6 chữ số"
                    edtPhone.requestFocus()
                }

                else -> {
                    registerUser(emailText, pass, username, phone)
                }
            }
        }
    }

    private fun registerUser(emaill: String, pass: String, uname: String, phone: String) {
        progressBar.visibility = View.VISIBLE
        btnRegister.isEnabled = false
        mAuth.createUserWithEmailAndPassword(emaill, pass).addOnCompleteListener { task ->
            progressBar.visibility = View.GONE
            btnRegister.isEnabled = true
            if (task.isSuccessful) {
                val user = mAuth.currentUser
                val email = user!!.email
                val uid = user!!.uid
                val hashMap = HashMap<Any, String?>()
                hashMap["uid"] = uid
                hashMap["name"] = uname
                hashMap["email"] = email
                hashMap["phone"] = phone
                hashMap["typingTo"] = "noOne"
                hashMap["role"] = "Customer"
                val database = FirebaseDatabase.getInstance()
                val reference = database.getReference("Users")
                reference.child(uid).setValue(hashMap)
                Toast.makeText(
                    this@RegisterActivity,
                    "Đăng ký thành công " + user!!.email,
                    Toast.LENGTH_LONG
                ).show()
                val mainIntent: Intent =
                    Intent(
                        this@RegisterActivity,
                        LoginActivity::class.java
                    )
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(mainIntent)
                finish()
            } else {
                Toast.makeText(this@RegisterActivity, "Đăng ký không thành công !", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            progressBar.visibility = View.GONE
            btnRegister.isEnabled = true
            Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
        }
    }


    // Extension function to validate email format
    private fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

    // Check if username contains special characters
    private fun isValidUsername(username: String): Boolean {
        val regex = "^[A-Za-z0-9_]+$"  // Allow only letters, digits, and underscores
        return username.matches(regex.toRegex())
    }
}
