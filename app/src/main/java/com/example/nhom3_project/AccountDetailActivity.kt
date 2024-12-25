package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccountDetailActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var edtUserName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtGender: AutoCompleteTextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var txtChangePassword: TextView
    private lateinit var btnSave: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_detail)
        setControl()
        setEvent()
    }

    private fun setControl() {
        mAuth = FirebaseAuth.getInstance()
        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnSave)
        txtChangePassword = findViewById(R.id.txtChangePassword)
        edtUserName = findViewById(R.id.edtUserName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhone = findViewById(R.id.edtPhone)
        edtGender = findViewById(R.id.edtGender)
    }

    private fun setEvent() {
        showProfile()
        val genderOptions = listOf("Nam", "Nữ")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        edtGender.setAdapter(adapter)

        edtGender.setOnClickListener {
            edtGender.showDropDown()
        }

        txtChangePassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        btnSave.setOnClickListener {
            saveProfileChanges()
        }
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showProfile() {
        val user = mAuth.currentUser
        if (user != null) {
            val uid = user.uid
            val database = FirebaseDatabase.getInstance()
            val reference = database.getReference("Users").child(uid)

            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        edtUserName.setText(snapshot.child("name").value.toString())
                        edtEmail.setText(snapshot.child("email").value.toString())
                        edtPhone.setText(snapshot.child("phone").value.toString())
                        edtGender.setText(snapshot.child("gender").value.toString(), false)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Không tìm thấy thông tin người dùng.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        applicationContext,
                        "Lỗi khi tải thông tin: ${error.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        } else {
            Toast.makeText(this, "Người dùng chưa đăng nhập.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfileChanges() {
        val user = mAuth.currentUser
        if (user != null) {
            val updatedUsername = edtUserName.text.toString().trim()
            val updatedPhone = edtPhone.text.toString().trim()
            val updatedGender = edtGender.text.toString().trim()

            when {
                updatedUsername.isEmpty() -> {
                    edtUserName.error = "Tên người dùng không được để trống"
                    edtUserName.requestFocus()
                    return
                }

                updatedPhone.isEmpty() -> {
                    edtPhone.error = "Số điện thoại không được để trống"
                    edtPhone.requestFocus()
                    return
                }

                updatedPhone.length < 6 -> {
                    edtPhone.error = "Số điện thoại phải có ít nhất 6 số"
                    edtPhone.requestFocus()
                    return
                }
            }

            val updates = hashMapOf<String, Any>(
                "name" to updatedUsername,
                "phone" to updatedPhone,
                "gender" to updatedGender
            )

            val database = FirebaseDatabase.getInstance()
            val reference = database.getReference("Users").child(user.uid)
            reference.updateChildren(updates).addOnCompleteListener { dbTask ->
                if (dbTask.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Cập nhật thông tin thành công!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Lỗi khi cập nhật thông tin trong Database.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(this, "Người dùng chưa đăng nhập.", Toast.LENGTH_SHORT).show()
        }
    }
}

