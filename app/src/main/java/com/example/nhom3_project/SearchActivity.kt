package com.example.nhom3_project

import Products
import Wishlist
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {
    private lateinit var btn_search: Button
    private lateinit var edt_search: EditText
    private lateinit var dbref: DatabaseReference
    private lateinit var adapter: SearachAdapter
    private var productList: MutableList<Products> = mutableListOf()
    private lateinit var ivBackSearch: ImageView
    private lateinit var navbarBott: BottomNavigationView
    private lateinit var recSearch: RecyclerView
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = firebaseAuth.currentUser
    private val uid = currentUser?.uid.toString()
    private var wishlList: MutableList<Wishlist> = mutableListOf()
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        setControl()
        getDataWislist {
            setEvent()
        }

        setEventNavBar()
        navbarBott.menu.findItem(R.id.nav_search).isChecked = true
    }
    private fun setControl(){
        ivBackSearch = findViewById(R.id.ivBackSearch)
        navbarBott = findViewById(R.id.bottom_navigationCart)
        recSearch = findViewById(R.id.recSearch)
        btn_search = findViewById(R.id.btn_search)
        edt_search = findViewById(R.id.edt_search)

    }
    private fun setEvent(){
        btn_search.setOnClickListener(){
            val searchText = edt_search.text.toString().trim().lowercase()
           searchProduct(searchText)
            Log.d("keyww",searchText)
        }
    }
    private fun searchProduct(query: String) {
        productList = mutableListOf()
        dbref = FirebaseDatabase.getInstance().reference
        dbref.child("products").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var num = 0
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val productName = productSnapshot.child("name").getValue(String::class.java)?.trim()?.lowercase() ?: ""
                    // Kiểm tra xem tên sản phẩm có chứa query hay không
                    if (productName.contains(query.trim().lowercase())) {
                        num++
                        Log.d("Num serch" , num++.toString())
                        val id =
                            productSnapshot.child("id").getValue(String::class.java) ?: ""
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
                if (num == 0) {
                    Toast.makeText(this@SearchActivity, "Không tìm thấy sản phẩm nào!", Toast.LENGTH_SHORT).show()
                }
                adapter = SearachAdapter(this@SearchActivity, productList)
                recSearch.layoutManager = GridLayoutManager(this@SearchActivity, 2)
                recSearch.adapter = adapter
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@SearchActivity,
                    "Lỗi khi lấy dữ liệu sản phẩm: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    private fun getDataWislist(onDataLoaded: () -> Unit) {
        dbRef = FirebaseDatabase.getInstance().reference

        dbRef.child("Wishlist").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                wishlList.clear() // Làm sạch danh sách trước khi thêm dữ liệu mới
                for (cartSnapshot in snapshot.children) {
                    val userid = cartSnapshot.child("userid").getValue(String::class.java) ?: ""
                    if (userid == uid) {
                        val id = cartSnapshot.child("id").getValue(String::class.java) ?: ""
                        val productid = cartSnapshot.child("productid").getValue(String::class.java) ?: ""
                        val select = cartSnapshot.child("selected").getValue(Boolean::class.java) ?: false
                        val wishlist = Wishlist(id, userid, productid,select)
                        wishlList.add(wishlist)
                    }
                }

                onDataLoaded()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeActivity", "Lỗi tải dữ liệu yêu thích: ${error.message}")
            }
        })
    }
    //cua the lo
    private fun addToWishlist(productId: String) {
        dbref.child("Wishlist").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val existingWishlist = snapshot.children.find {
                    val wishlistUserId = it.child("userid").getValue(String::class.java) ?: ""
                    val wishlistProductId = it.child("productid").getValue(String::class.java) ?: ""
                    wishlistUserId == uid && wishlistProductId == productId
                }
                if (existingWishlist == null) {
                    val WishlistId = dbref.child("Wishlist").push().key ?: return
                    val wishlistItem = Wishlist(
                        id = WishlistId,
                        userid = uid,
                        productid = productId,
                        selected = true,
                    )
                    dbRef.child("Wishlist").child(WishlistId).setValue(wishlistItem)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@SearchActivity,
                                "Sản phẩm đã được thêm vào yêu thích!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@SearchActivity,
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
                                this@SearchActivity,
                                "Cập nhật thành công!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@SearchActivity,
                                "Lỗi khi cập nhật số lượng: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SearchActivity, "Lỗi: ${error.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
    private fun eventBack(){
        ivBackSearch.setOnClickListener{
            finish()
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
            R.id.nav_wishlist -> {
                val intent = Intent(this, WishListActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.nav_shoppingcart -> {
                // Chuyển
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)

                true
            }

            R.id.nav_account -> {

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