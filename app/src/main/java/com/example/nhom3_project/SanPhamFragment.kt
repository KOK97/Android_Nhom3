package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class SanPhamFragment : Fragment() {
    private lateinit var btnSua: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_san_pham, container, false)

        // Lấy nút "+Add"
        val addButton = view.findViewById<Button>(R.id.addButton)

        // Xử lý sự kiện khi bấm nút "+Add"
        addButton.setOnClickListener {
            // Chuyển sang trang thêm sản phẩm
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, AddProductFragment()) // Replace with AddProductFragment
            transaction.addToBackStack(null) // Add transaction to back stack
            transaction.commit()
        }

        // Lấy nút "Sửa" và xử lý sự kiện bấm
        btnSua = view.findViewById(R.id.suaSanPham)
        btnSua.setOnClickListener {
            // Chuyển sang activity sửa sản phẩm
            val intent = Intent(requireContext(), EditProduct::class.java)
            startActivity(intent)
        }

        return view
    }
}
