package com.example.nhom3_project

import Customer
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class EditCustomer : AppCompatActivity() {
    private lateinit var edt_user_id: EditText
    private lateinit var edt_user_name: EditText
    private lateinit var edt_phone: EditText
    private lateinit var edt_email: EditText
    private var code: String = ""
    private lateinit var spinner_role: Spinner
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_customer)
        setControl()
        setEvent()
    }
    private fun setControl(){
        edt_user_id = findViewById(R.id.edt_user_id)
        edt_user_name = findViewById(R.id.edt_user_name)
        edt_phone = findViewById(R.id.edt_phone)
        edt_email = findViewById(R.id.edt_email)
        spinner_role = findViewById(R.id.spinner_role)
        btnSave = findViewById(R.id.btn_save_customer)
    }
    private fun setEvent(){
        val role = listOf("Admin", "Customer")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, role)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_role.adapter = adapter


        val product = intent.getParcelableExtra<Customer>("customer_data")
        product?.let {
            code = it.uid
            edt_user_id.setText(it.uid)
            edt_user_name.setText(it.name)
            edt_phone.setText(it.phone)
            edt_email.setText(it.email)

            val rolePosition = role.indexOf(it.role)
            spinner_role.setSelection(rolePosition)
        }

        btnSave.setOnClickListener {
            val updatedCustomer = Customer(
                uid = code,
                name = edt_user_name.text.toString(),
                phone = edt_phone.text.toString(),
                email = edt_email.text.toString(),
                role = spinner_role.selectedItem.toString(),

            )

            if (updatedCustomer.name.isEmpty() || updatedCustomer.role.isEmpty() || updatedCustomer.phone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            } else {
                updateCustomerInFirebase(updatedCustomer)
            }
        }
    }
    private fun updateCustomerInFirebase(customer: Customer) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")

        myRef.child(customer.uid).setValue(customer) { error, _ ->
            if (error == null) {
                Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show()
            }
        }
    }
}