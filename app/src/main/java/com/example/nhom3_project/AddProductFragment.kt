package com.example.nhom3_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class AddProductFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.addproduct, container, false)

        // Nút thêm sản phẩm
        val addProductButton = view.findViewById<Button>(R.id.addProductButton)
        addProductButton.setOnClickListener {
            // Xử lý thêm sản phẩm (nếu cần)
            Toast.makeText(requireContext(), "Đã thêm sản phẩm!", Toast.LENGTH_SHORT).show()

            // Quay lại `SanPhamFragment`
            parentFragmentManager.popBackStack()
        }

        return view
    }
}
