package com.example.nhom3_project

import Products
import Wishlist
import WishlistAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WishListActivity : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var uid: String
    private lateinit var adapter: WishlistAdapter
    private var productList: MutableList<Products> = mutableListOf()
    private var wList: MutableList<Wishlist> = mutableListOf()

    private lateinit var navbarBott: BottomNavigationView
    private lateinit var rvWishlist: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wish_list)

        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        uid = currentUser?.uid.toString()

        setControll()
        setEventNavBar()
        getDataWishlist {
            setDataWishlist()
        }
        setEvenAddtoWishlist()
        navbarBott.menu.findItem(R.id.nav_wishlist).isChecked = true
    }
    private fun setControll(){
        rvWishlist = findViewById(R.id.rvWishlist)
        navbarBott = findViewById(R.id.bottom_navigationCart)
    }
    private fun getDataWishlist(onDataLoaded: () -> Unit) {
        wList = mutableListOf()
        dbRef = FirebaseDatabase.getInstance().reference

        dbRef.child("Wishlist").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                wList.clear()
                for (cartSnapshot in snapshot.children) {
                    val userid = cartSnapshot.child("userid").getValue(String::class.java) ?: ""
                    if (userid==uid){
                        val select = cartSnapshot.child("selected").getValue(Boolean::class.java) ?: false
                       if (select == true){
                           val id = cartSnapshot.child("id").getValue(String::class.java) ?: ""
                           val productid =
                               cartSnapshot.child("productid").getValue(String::class.java) ?: ""
                           val wishl = Wishlist(id, userid, productid)
                           wList.add(wishl)
                       }
                    }
                }
                onDataLoaded()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@WishListActivity,
                    "Lỗi khi lấy dữ liệu từ Cart: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    private fun setDataWishlist() {
        productList = mutableListOf()
        dbRef = FirebaseDatabase.getInstance().reference
        val productIds = wList.map { it.productid }.toSet()

        dbRef.child("products").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val id = productSnapshot.child("id").getValue(String::class.java) ?: ""
                    if (id in productIds) {
                        val name =
                            productSnapshot.child("name").getValue(String::class.java) ?: "No Name"
                        val category =
                            productSnapshot.child("category").getValue(String::class.java) ?: ""
                        val type = productSnapshot.child("type").getValue(String::class.java) ?: ""
                        val price = productSnapshot.child("price").getValue(Int::class.java) ?: 0
                        val desc = productSnapshot.child("desc").getValue(String::class.java) ?: ""
                        val img =
                            productSnapshot.child("imageUrl").getValue(String::class.java) ?: ""

                        productList.add(
                            Products(
                                id, name, category, type, price, 1, desc, img
                            )
                        )
                    }
                }
                adapter = WishlistAdapter(this@WishListActivity, productList)
                rvWishlist.layoutManager = GridLayoutManager(this@WishListActivity, 3)
                rvWishlist.adapter = adapter
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@WishListActivity,
                    "Lỗi khi lấy dữ liệu sản phẩm: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    private fun setEvenAddtoWishlist() {
        val dataproductid = intent.getStringExtra("productID")
        if (dataproductid != null) {
            addToWishlist(dataproductid.toString())
        }

    }
    private fun addToWishlist(productId: String) {
        dbRef.child("Wishlist").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val existingWishlist = snapshot.children.find {
                    val wishlistUserId = it.child("userid").getValue(String::class.java) ?: ""
                    val wishlistProductId = it.child("productid").getValue(String::class.java) ?: ""
                    wishlistUserId == uid && wishlistProductId == productId
                }
                if (existingWishlist == null) {
                    val WishlistId = dbRef.child("Wishlist").push().key ?: return
                    val wishlistItem = Wishlist(
                        id = WishlistId,
                        userid = uid,
                        productid = productId,
                        selected = true,
                    )
                    dbRef.child("Wishlist").child(WishlistId).setValue(wishlistItem)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@WishListActivity,
                                "Sản phẩm đã được thêm vào yêu thích!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@WishListActivity,
                                "Lỗi khi thêm yêu thích: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }else {
                    val wishlistId = existingWishlist.key ?: return
                    val Selectted =
                        existingWishlist.child("selected").getValue(Boolean::class.java) ?: false

                    if (Selectted == false){
                        dbRef.child("Wishlist").child(wishlistId).child("selected")
                            .setValue(true)
                    } else{
                        dbRef.child("Wishlist").child(wishlistId).child("selected")
                            .setValue(false)
                    }
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@WishListActivity,
                                "Cập nhật số lượng thành công!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@WishListActivity,
                                "Lỗi khi cập nhật số lượng: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WishListActivity, "Lỗi: ${error.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
    private fun setEventNavBar() {
        navbarBott.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_search -> {
                    true
                }

                R.id.nav_shoppingcart -> {
                    // Chuyển
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)

                    true
                }

                R.id.nav_account -> {
                    // Xử lý khi chọn Profile
                    // Chuyển
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }
}