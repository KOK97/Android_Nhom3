package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.ListView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class CartActivity : AppCompatActivity() {
    private lateinit var ivBackCart: ImageView
    private lateinit var btnPay: Button
    private lateinit var navbarBott: BottomNavigationView
    private lateinit var lvCart: ListView
    private lateinit var adapter: CartAdapter
    private lateinit var productList: MutableList<Products>
    private lateinit var cartList: MutableList<Cart>
    private lateinit var totalTextView: TextView
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        setControll()
        // Đợi dữ liệu từ Firebase trước khi tiếp tục
        getDataCart {
            setDataCart()
            updateTotalPrice()
        }
        setDataCart()
        setEventNavBar()
        setEventBack()
        setEventPay()

        navbarBott.menu.findItem(R.id.nav_shoppingcart).isChecked = true
        updateTotalPrice()
    }

    private fun setControll() {
        ivBackCart = findViewById(R.id.ivBackCart)
        navbarBott = findViewById(R.id.bottom_navigationCart)
        btnPay = findViewById(R.id.btnThanhToan)
        lvCart = findViewById(R.id.lvCart)
        totalTextView = findViewById(R.id.tvTongTienCart)
    }
    private fun getDataCart(onDataLoaded: () -> Unit) {
        cartList = mutableListOf()
        dbRef = FirebaseDatabase.getInstance().reference

        dbRef.child("Cart").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartList.clear()
                for (cartSnapshot in snapshot.children) {
                    val id = cartSnapshot.child("id").getValue(Int::class.java) ?: 0
                    val userid = cartSnapshot.child("userid").getValue(Int::class.java) ?: 0
                    val productid = cartSnapshot.child("productid").getValue(Int::class.java) ?: 0
                    val quantity = cartSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                    val cart = Cart(id, userid, productid, quantity)
                    cartList.add(cart)
                }

                // Gọi hàm callback sau khi dữ liệu đã được tải xong
                onDataLoaded()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CartActivity, "Lỗi khi lấy dữ liệu từ Cart: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun setDataCart() {
        productList = mutableListOf()

        // Khởi tạo Realtime Database Reference
        dbRef = FirebaseDatabase.getInstance().reference

        // Lấy dữ liệu Product
        dbRef.child("Product").addValueEventListener(object : ValueEventListener {
            val productIds = cartList.map { it.productid }.toSet()
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()

                for (productSnapshot in snapshot.children) {
                    val id = productSnapshot.child("id").getValue(Int::class.java) ?: 0
                    if (id in productIds ){
                        val name = productSnapshot.child("name").getValue(String::class.java) ?: "No Name"
                        val price = productSnapshot.child("price").getValue(Double::class.java) ?: 0.0
                        val img = productSnapshot.child("img").getValue(String::class.java) ?: ""
                        // Thêm sản phẩm vào danh sách
                        productList.add(Products(id, name, price, img))
                    }
                }

                // Gán adapter sau khi đã có dữ liệu
                adapter = CartAdapter(this@CartActivity, productList) { updateTotalPrice() }
                lvCart.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CartActivity, "Lỗi khi lấy và gán dữ liệu Product vào Cart: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setEventPay() {
        btnPay.setOnClickListener {
            val intent = Intent(this, PayActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setEventBack() {
        ivBackCart.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setEventNavBar() {
        navbarBott.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_search -> true
                R.id.nav_wishlist -> true
                R.id.nav_account -> {
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun updateTotalPrice() {
        val total = productList.filter { it.isSelected }.sumByDouble { it.price * it.quantity }
        totalTextView.text = "Total: $total VND"
    }
}
