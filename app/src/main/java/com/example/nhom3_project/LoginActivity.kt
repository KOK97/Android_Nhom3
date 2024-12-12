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
import com.google.firebase.database.FirebaseDatabase


class LoginActivity : AppCompatActivity() {

    private lateinit var txtRegister: TextView
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
        btnLogin.setOnClickListener() {
            val email: String = edtEmail.getText().toString().trim()
            val pass: String = edtPassword.getText().toString().trim()

            // Kiểm tra email, mật khẩu và tên người dùng
            when {
                !email.isValidEmail() -> {
                    edtEmail.error = "Email không hợp lệ"
                    edtEmail.requestFocus()
                }
                
                else -> {
                    loginUser(email, pass)
                }
            }
        }
        txtRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(emaill: String, pass: String) {
        progressBar.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(emaill, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressBar.visibility = View.GONE
                val user = mAuth.currentUser
                if (task.result.additionalUserInfo!!.isNewUser) {
                    val email = user!!.email
                    val uid = user!!.uid
                    val hashMap = HashMap<Any, String?>()
                    hashMap["email"] = email
                    hashMap["uid"] = uid
                    hashMap["name"] = ""
                    hashMap["typingTo"] = "noOne"
                    hashMap["phone"] = ""
                    val database = FirebaseDatabase.getInstance()
                    val reference = database.getReference("Users")
                    reference.child(uid).setValue(hashMap)
                }
                Toast.makeText(
                    this@LoginActivity,
                    "Đăng nhập thành công " + user!!.email,
                    Toast.LENGTH_LONG
                ).show()
                val mainIntent = Intent(
                    this@LoginActivity,
                    HomeActivity::class.java
                )
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(mainIntent)
                finish()
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Đăng nhập không thành công !",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }.addOnFailureListener {
            progressBar.visibility = View.GONE
            Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()
}