package com.example.nhom3_project

import Products
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase

class AddProduct : Fragment() {

    private lateinit var edt_name: EditText
    private lateinit var edt_code: EditText
    private lateinit var spinner_category: Spinner
    private lateinit var edt_price: EditText
    private lateinit var edt_quantity: EditText
    private lateinit var edt_desc: EditText
    private lateinit var edt_imageUrl: EditText
    private lateinit var btn_save_product: Button
    private lateinit var radio_group: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_product, container, false)

        edt_name = view.findViewById(R.id.edt_product_name)
        edt_code = view.findViewById(R.id.edt_product_code)
        spinner_category = view.findViewById(R.id.spinner_category)
        radio_group = view.findViewById(R.id.rdo_group)
        edt_price = view.findViewById(R.id.edt_price)
        edt_quantity = view.findViewById(R.id.edt_quantity)
        edt_desc = view.findViewById(R.id.edt_desc)
        edt_imageUrl = view.findViewById(R.id.edt_image_url)
        btn_save_product = view.findViewById(R.id.btn_save_product)

        setupCategorySpinner()

        btn_save_product.setOnClickListener {
            saveProduct()
        }

        return view
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf("Jewelry", "Watches", "Earrings", "Rings")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)

        spinner_category.adapter = adapter
    }

    private fun saveProduct() {
        val name = edt_name.text.toString().trim()
        val code = edt_code.text.toString().trim()
        val category = spinner_category.selectedItem.toString()

        val type = when (radio_group.checkedRadioButtonId) {
            R.id.radio_nam -> "Male"
            R.id.radio_nu -> "Female"
            else -> ""
        }

        val price = edt_price.text.toString().trim().toIntOrNull() ?: 0
        val quantity = edt_quantity.text.toString().trim().toIntOrNull() ?: 0
        val discountCode = edt_desc.text.toString().trim()
        val imageUrl = edt_imageUrl.text.toString().trim()

        if (name.isEmpty() || code.isEmpty() || category.isEmpty() || price == 0 || discountCode.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val product = Products(code, name, category, type, price,quantity ,discountCode, imageUrl)

        addProductToFirebase(product)
    }

    private fun addProductToFirebase(product: Products) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("products")

        val pathObject = product.id
        myRef.child(pathObject).setValue(product) { error, _ ->
            if (error == null) {
                Toast.makeText(requireContext(), "Sản phẩm đã được thêm thành công", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Lỗi khi thêm sản phẩm", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
