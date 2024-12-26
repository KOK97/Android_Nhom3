package com.example.nhom3_project

import Products
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class EditProduct : AppCompatActivity() {

    private lateinit var edtProductName: EditText
    private lateinit var edtProductCode: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var edtPrice: EditText
    private lateinit var edtQuantity: EditText
    private lateinit var edtDesc: EditText
    private lateinit var edtImageUrl: EditText
    private lateinit var btnSave: Button
    private lateinit var radio_group: RadioGroup
    private var productId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_product)

        edtProductName = findViewById(R.id.edt_product_name)
        edtProductCode = findViewById(R.id.edt_product_code)
        spinnerCategory = findViewById(R.id.spinner_category)
        radio_group = findViewById(R.id.rdo_group)
        edtPrice = findViewById(R.id.edt_price)
        edtQuantity = findViewById(R.id.edt_quantity)
        edtDesc = findViewById(R.id.edt_desc)
        edtImageUrl = findViewById(R.id.edt_image_url)
        btnSave = findViewById(R.id.btn_save_product)


        val categories = listOf("Jewelry", "Watches", "Earrings", "Rings")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter


        val product = intent.getParcelableExtra<Products>("product_data")
        product?.let {
            productId = it.id
            edtProductName.setText(it.name)
            edtProductCode.setText(it.id)

            val categoryPosition = categories.indexOf(it.category)
            spinnerCategory.setSelection(categoryPosition)
            edtPrice.setText(it.price.toString())
            edtQuantity.setText(it.quantity.toString())
            edtDesc.setText(it.desc)
            edtImageUrl.setText(it.imageUrl)
            when (it.type) {
                "Female" -> radio_group.check(R.id.radio_nu)
                "Male" -> radio_group.check(R.id.radio_nam)
                else -> radio_group.clearCheck()
            }
        }

        btnSave.setOnClickListener {
            val updatedProduct = Products(
                id = productId,
                name = edtProductName.text.toString(),
                category = spinnerCategory.selectedItem.toString(),
                price = edtPrice.text.toString().toIntOrNull() ?: 0,
                quantity = edtQuantity.text.toString().toIntOrNull() ?: 0,
                desc = edtDesc.text.toString(),
                imageUrl = edtImageUrl.text.toString(),
                type = when (radio_group.checkedRadioButtonId) {
                    R.id.radio_nu -> "Female"
                    R.id.radio_nam -> "Male"
                    else -> "No option selected"
                }
            )

            if (updatedProduct.name.isEmpty() || updatedProduct.category.isEmpty() || updatedProduct.price <= 0) {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            } else {
                updateProductInFirebase(updatedProduct)
            }
        }
    }


    private fun updateProductInFirebase(product: Products) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("products")

        myRef.child(product.id).setValue(product) { error, _ ->
            if (error == null) {
                Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
