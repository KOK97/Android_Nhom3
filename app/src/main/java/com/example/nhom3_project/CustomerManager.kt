package com.example.nhom3_project

import Customer
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

class CustomerManager : Fragment() {

    private lateinit var userGridView: GridView
    private lateinit var searchBar: EditText
    private lateinit var btnSearch: Button
    private lateinit var userAdapter: CustomerAdapter
    private lateinit var userList: MutableList<Customer>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.customer_manager, container, false)

        setControl(view)
        setEvent()
        loadCustomers()

        return view
    }

    private fun setControl(view: View) {
        userGridView = view.findViewById(R.id.userGridView)
        searchBar = view.findViewById(R.id.edt_search_customer)
        btnSearch = view.findViewById(R.id.btn_search_customer)

        userList = mutableListOf()
        userAdapter = CustomerAdapter(requireContext(), userList, { customerToDelete ->
            showDeleteConfirmationDialog(customerToDelete)
        },
            { selectedCustomer ->
                val intent = Intent(requireContext(), EditCustomer::class.java)
                intent.putExtra("customer_data", selectedCustomer)
                startActivity(intent)
            },)
        userGridView.adapter = userAdapter
    }

    private fun setEvent() {
        btnSearch.setOnClickListener {
            val query = searchBar.text.toString().trim()
            if (query.isNotEmpty()) {
                searchCustomer(query)
            }
        }
    }

    private fun loadCustomers() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (snapshot in dataSnapshot.children) {
                    val customer = snapshot.getValue(Customer::class.java)
                    customer?.let {
                        userList.add(it)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load customers", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchCustomer(query: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")

        myRef.orderByChild("name").startAt(query).endAt(query + "\uf8ff").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (snapshot in dataSnapshot.children) {
                    val customer = snapshot.getValue(Customer::class.java)
                    customer?.let {
                        userList.add(it)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteConfirmationDialog(customer: Customer) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Xóa người dùng")
            .setMessage("Bạn có chắc chắn muốn xóa người dùng ${customer.name}?")
            .setPositiveButton("Xóa") { _, _ ->
                deleteCustomerFromFirebase(customer)
            }
            .setNegativeButton("Hủy", null)
            .create()
        dialog.show()
    }

    private fun deleteCustomerFromFirebase(customer: Customer) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")

        myRef.child(customer.uid).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Customer deleted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete customer", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

