package com.example.doan123

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class SanPhamFragment : Fragment() {

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
            transaction.replace(R.id.fragment_container, AddProductFragment()) // Thay bằng fragment hiển thị giao diện thêm
            transaction.addToBackStack(null) // Để quay lại trang trước đó nếu cần
            transaction.commit()
        }

        return view
    }
}
