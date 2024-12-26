package com.example.nhom3_project

import Products
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {
    private lateinit var btn_search: Button
    private lateinit var edt_search: EditText
//    private lateinit var lnSearchPro : LinearLayout
    private lateinit var dbref: DatabaseReference

    private lateinit var adapter: SearachAdapter
    private var productList: MutableList<Products> = mutableListOf()

    private lateinit var navbarBott: BottomNavigationView
    private lateinit var recSearch: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        setControl()

      setEvent()
    }
    private fun setControl(){
//        lnSearchPro = findViewById(R.id.lnSearch)
        recSearch = findViewById(R.id.recSearch)
        btn_search = findViewById(R.id.btn_search)
        edt_search = findViewById(R.id.edt_search)

    }
    private fun setEvent(){
        btn_search.setOnClickListener(){
            val searchText = edt_search.text.toString().trim().lowercase()
           setDataWishlist(searchText)
            Log.d("keyww",searchText)
        }
    }
    private fun setDataWishlist(query: String) {
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

//    private fun searchProducts(query: String) {
//        databaseReference = FirebaseDatabase.getInstance().getReference("products")
//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val inflater = LayoutInflater.from(this@SearchActivity)
//                val view = inflater.inflate(R.layout.search_item, lnSearchPro, false)
//                val productList = snapshot.children.toList()
//
//                for (productSnapshot in productList) {
//                    var productName = productSnapshot.child("name").getValue(String::class.java) ?: "No name"
//                   productName =  productName.trim().lowercase()
//                    if (productName.contains(query,ignoreCase = true)) {
//                        lnSearchPro.removeAllViews()
////                        val product_id = productSnapshot.child("id").getValue(String::class.java) ?: "0"
//
//                        val productPrice = productSnapshot.child("price").getValue(Int::class.java) ?: "No price"
//                        val imageUrl = productSnapshot.child("imageUrl").getValue(String::class.java) ?: "No image"
//                        //set data sp2
//                        //lay anh tu drawble
//                        val imageResId = resources.getIdentifier(imageUrl, "drawable", packageName)
//
//                        if (imageResId != 0) {
//                            //neu co id anh trong drawble thi load len
//                            Glide.with(this@SearchActivity)
//                                .load(imageResId)
//                                .override(150, 100)
//                                .into(view.findViewById<ImageView>(R.id.imageView))
//                        }
//                        else{
//                            //lay anh truc tiep tu url
//                            Glide.with(this@SearchActivity)
//                                .load("$imageUrl")
//                                .override(150,100)
//                                .into(view.findViewById<ImageView>(R.id.imageView))
//                        }
//
//                        view.findViewById<TextView>(R.id.tvNamePr).text = productName
////                        view.findViewById<TextView>(R.id.tvId).text = product_id1
//                        view.findViewById<TextView>(R.id.tvPrice).text = "$productPrice VND"
//                        lnSearchPro.addView(view)
//
//
//                            Log.d("get sp search",productName)
//                        }
//                }
//
//                // Cập nhật danh sách sản phẩm vào Adapter
////                searchAdapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("ProductSearch", "Error getting data: ${error.message}")
//            }
//        })
//    }
    private fun setEventNavBar() {
        navbarBott.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
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