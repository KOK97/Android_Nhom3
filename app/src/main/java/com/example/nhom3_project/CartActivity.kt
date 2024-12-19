package com.example.nhom3_project

import Products
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.ListView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
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
    private var productList: MutableList<Products> = mutableListOf()
    private lateinit var cartList: MutableList<Cart>
    private var datapay: MutableList<PayData> = mutableListOf()
    private lateinit var totalTextView: TextView
    private lateinit var dbRef: DatabaseReference
    private lateinit var uid: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        dbRef = FirebaseDatabase.getInstance().reference
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        uid = currentUser?.uid.toString()
        setControll()

            getDataCart {
                if (cartList.isNotEmpty()) {
                    setDataCart {
                        updateTotalPrice()
                    }
                }
            }
        setEventAdd()
        setEventNavBar()
        setEventBack()
        setEventPayAccept()
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

    private fun setEventAdd() {
        val dataproductid = intent.getStringExtra("productClick")
        if (dataproductid != null) {
            addToCart(dataproductid.toString())
        }

    }

    private fun setEventPayAccept() {
        btnPay.setOnClickListener {
            val ProductsSelected = productList.filter { it.isSelected }
            val total = productList.filter { it.isSelected }
                .sumByDouble { (it.price * it.quantity).toDouble() }
            if (ProductsSelected.isNotEmpty()) {
                datapay.clear()
                for (pro in ProductsSelected) {
                    val cart = cartList.filter { it.productid == pro.id }
                    for (ca in cart) {
                        datapay.add(PayData(ca.id, pro.id, pro.name, ca.quantity))
                    }
                }
                val intent = Intent(this, PayActivity::class.java)
                intent.putExtra("product_list", ArrayList(datapay))
                intent.putExtra("totalcart", total.toString())
                startActivity(intent)
            }
        }
    }

    private fun getDataCart(onDataLoaded: () -> Unit) {
        cartList = mutableListOf()
        dbRef = FirebaseDatabase.getInstance().reference

        dbRef.child("Carts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartList.clear()
                for (cartSnapshot in snapshot.children) {
                    val userid = cartSnapshot.child("userid").getValue(String::class.java) ?: ""
                   if (userid==uid){
                       val id = cartSnapshot.child("id").getValue(String::class.java) ?: ""
                       val productid =
                           cartSnapshot.child("productid").getValue(String::class.java) ?: ""
                       val quantity = cartSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                       val cart = Cart(id, userid, productid, quantity)
                       cartList.add(cart)
                   }
                }
                // Gọi hàm callback sau khi dữ liệu đã được tải xong
                onDataLoaded()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@CartActivity,
                    "Lỗi khi lấy dữ liệu từ Cart: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setDataCart(function: () -> Unit) {
        productList = mutableListOf()

        // Khởi tạo
        dbRef = FirebaseDatabase.getInstance().reference

        // Lấy dữ liệu
        dbRef.child("products").addValueEventListener(object : ValueEventListener {
            val productIds = cartList.map { it.productid }.toSet()
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val id = productSnapshot.child("id").getValue(String::class.java) ?: ""
                    if (id in productIds) {
                        val name =
                            productSnapshot.child("name").getValue(String::class.java) ?: "No Name"

                        val category = productSnapshot.child("category").getValue(String::class.java) ?: ""
                        val type = productSnapshot.child("type").getValue(String::class.java) ?: ""
                        val price = productSnapshot.child("price").getValue(Int::class.java) ?: 0
                        val desc = productSnapshot.child("desc").getValue(String()::class.java) ?:""

                        val img =
                            productSnapshot.child("imageUrl").getValue(String::class.java) ?: ""


                        val cartItem = cartList.find { it.productid == id }
                        val quantity = cartItem?.quantity ?: 1

                        productList.add(
                            Products(
                               id,name,category,type,price,quantity,desc,img
                            )
                        )
                    }
                }
                adapter = CartAdapter(this@CartActivity, productList) { updateTotalPrice() }
                lvCart.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@CartActivity,
                    "Lỗi khi lấy và gán dữ liệu Product vào Cart: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun addToCart(productId: String) {
        dbRef.child("Carts").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val existingCart = snapshot.children.find {
                    val cartUserId = it.child("userid").getValue(String::class.java) ?: ""
                    val cartProductId = it.child("productid").getValue(String::class.java) ?: ""
                    cartUserId == uid && cartProductId == productId
                }
                if (existingCart == null) {
                    val cartId = dbRef.child("Carts").push().key ?: return
                    val cartItem = Cart(
                        id = cartId,
                        userid = uid,
                        productid = productId,
                        quantity = 1
                    )
                    dbRef.child("Carts").child(cartId).setValue(cartItem)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@CartActivity,
                                "Sản phẩm đã được thêm vào giỏ hàng!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@CartActivity,
                                "Lỗi khi thêm sản phẩm: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    val cartId = existingCart.key ?: return
                    val currentQuantity =
                        existingCart.child("quantity").getValue(Int::class.java) ?: 1
                    dbRef.child("Carts").child(cartId).child("quantity")
                        .setValue(currentQuantity + 1)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@CartActivity,
                                "Cập nhật số lượng thành công!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@CartActivity,
                                "Lỗi khi cập nhật số lượng: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CartActivity, "Lỗi: ${error.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
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
        val total =
            productList.filter { it.isSelected }.sumByDouble { (it.price * it.quantity).toDouble() }
        totalTextView.text = "Total: $total VND"
    }
}

