package com.example.nhom3_project

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class AccountDetailActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var edtUserName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_detail)
        setControl()
        setEvent()
    }

    private fun setControl() {
        btnBack = findViewById(R.id.btnBack)
        edtUserName = findViewById(R.id.edtUserName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhone = findViewById(R.id.edtPhone)
    }

    private fun setEvent() {
        mAuth = FirebaseAuth.getInstance()
        showProfile()
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun showProfile() {
        val user = mAuth.currentUser
        if (user != null) {
            val uid = user.uid
            val database = FirebaseDatabase.getInstance()
            val reference = database.getReference("Users").child(uid)

            // Đọc dữ liệu từ database
            reference.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val username = snapshot.child("name").value.toString()
                    val email = snapshot.child("email").value.toString()
                    val phone = snapshot.child("phone").value.toString()

                    edtUserName.setText(username)
                    edtEmail.setText(email)
                    edtPhone.setText(phone)
                } else {
                    Toast.makeText(this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Lỗi khi tải thông tin: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(this, "Người dùng chưa đăng nhập.", Toast.LENGTH_SHORT).show()
        }
    }

}