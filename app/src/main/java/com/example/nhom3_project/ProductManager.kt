package com.example.nhom3_project

import Products
import ProductAdapter
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductManager : Fragment() {

    private lateinit var btn_add_product: Button
    private lateinit var btn_search: Button
    private lateinit var edt_search: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: MutableList<Products>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.product_manager, container, false)

        setControl(view)
        setEvent()
        swipeRefreshLayout.setOnRefreshListener {
            loadProducts()
            swipeRefreshLayout.isRefreshing = false
        }
        loadProducts()

        return view
    }

    private fun setControl(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        btn_add_product = view.findViewById(R.id.btn_add_product)
        recyclerView = view.findViewById(R.id.recyclerView)
        btn_search = view.findViewById(R.id.btn_search)
        edt_search = view.findViewById(R.id.edt_search)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        productList = mutableListOf()
        productAdapter = ProductAdapter(requireContext(), productList,
            { selectedProduct ->
                val intent = Intent(requireContext(), EditProduct::class.java)
                intent.putExtra("product_data", selectedProduct)
                startActivity(intent)
            },
            { productToDelete ->
                showDeleteConfirmationDialog(productToDelete)
            }
        )

        recyclerView.adapter = productAdapter
    }

    private fun setEvent() {
        btn_add_product.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val addProductFragment = AddProduct()
            transaction.replace(R.id.fragment_container, addProductFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        btn_search.setOnClickListener {
            val searchText = edt_search.text.toString().trim().lowercase()
            val filteredProducts = productList.filter { product ->
                product.id.lowercase().contains(searchText) || product.name.lowercase()
                    .contains(searchText)
            }

            productAdapter.updateProducts(filteredProducts)
        }
    }

    private fun loadProducts() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("products")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                productList.clear()
                for (snapshot in dataSnapshot.children) {
                    val product = snapshot.getValue(Products::class.java)
                    product?.let {
                        productList.add(it)
                    }
                }
                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load products", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun showDeleteConfirmationDialog(product: Products) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Xác nhận xóa")
        builder.setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
        builder.setPositiveButton("Xóa") { _, _ ->
            deleteProductFromFirebase(product)
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    private fun deleteProductFromFirebase(product: Products) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("products")

        myRef.child(product.id).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Product deleted successfully", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete product", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}




