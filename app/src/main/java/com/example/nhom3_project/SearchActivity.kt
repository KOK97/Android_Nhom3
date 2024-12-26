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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {
    private lateinit var btn_search: Button
    private lateinit var edt_search: EditText
    private lateinit var lnSearchPro : LinearLayout
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        setControl()
//        searchProducts("nhan")
      setEvent()
    }
    private fun setControl(){
        lnSearchPro = findViewById(R.id.lnSearch)
//        recSearch = findViewById(R.id.recSearch)
        btn_search = findViewById(R.id.btn_search)
        edt_search = findViewById(R.id.edt_search)

    }
    private fun setEvent(){

        btn_search.setOnClickListener(){
            val searchText = edt_search.text.toString().trim().lowercase()
            searchProducts(searchText)
            Log.d("keyww",searchText)
        }
    }
    private fun searchProducts(query: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("products")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val inflater = LayoutInflater.from(this@SearchActivity)
                val view = inflater.inflate(R.layout.search_item, lnSearchPro, false)
                val productList = snapshot.children.toList()

                for (productSnapshot in productList) {
                    var productName = productSnapshot.child("name").getValue(String::class.java) ?: "No name"
                   productName =  productName.trim().lowercase()
                    if (productName.contains(query,ignoreCase = true)) {
                        lnSearchPro.removeAllViews()
//                        val product_id = productSnapshot.child("id").getValue(String::class.java) ?: "0"

                        val productPrice = productSnapshot.child("price").getValue(Int::class.java) ?: "No price"
                        val imageUrl = productSnapshot.child("imageUrl").getValue(String::class.java) ?: "No image"
                        //set data sp2
                        //lay anh tu drawble
                        val imageResId = resources.getIdentifier(imageUrl, "drawable", packageName)

                        if (imageResId != 0) {
                            //neu co id anh trong drawble thi load len
                            Glide.with(this@SearchActivity)
                                .load(imageResId)
                                .override(150, 100)
                                .into(view.findViewById<ImageView>(R.id.imageView))
                        }
                        else{
                            //lay anh truc tiep tu url
                            Glide.with(this@SearchActivity)
                                .load("$imageUrl")
                                .override(150,100)
                                .into(view.findViewById<ImageView>(R.id.imageView))
                        }

                        view.findViewById<TextView>(R.id.tvNamePr).text = productName
//                        view.findViewById<TextView>(R.id.tvId).text = product_id1
                        view.findViewById<TextView>(R.id.tvPrice).text = "$productPrice VND"
                        lnSearchPro.addView(view)


                            Log.d("get sp search",productName)
                        }
                }

                // Cập nhật danh sách sản phẩm vào Adapter
//                searchAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProductSearch", "Error getting data: ${error.message}")
            }
        })
    }
}