package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditProduct : AppCompatActivity() {
    private lateinit var btnEdit : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_product)
        btnEdit = findViewById(R.id.editProductButton)

        btnEdit.setOnClickListener{
            Toast.makeText(this, "Đã sửa sản phẩm!", Toast.LENGTH_SHORT).show()

            finish()
        }

    }

}