package com.example.nhom3_project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductDetail : AppCompatActivity() {
    private lateinit var tvNameDetail: TextView
    private lateinit var tvPriceDetail: TextView
    private lateinit var tvIdDetait: TextView
    private lateinit var tvType: TextView
    private lateinit var tvDesc: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvTotalCMT : TextView

    private lateinit var ivProDetail: ImageView
    private lateinit var ivDefault: ImageView
    private lateinit var ivBackDetail: ImageView

    private lateinit var btnExit :Button
    private lateinit var btnAddCart :Button
    private lateinit var btnBuyNow :Button
    private lateinit var btnComment :Button

    private lateinit var ratingBar : RatingBar
    private lateinit var lnBoxCMT : LinearLayout

    //nav
    private lateinit var navbarBott: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)
        setControl()
        setEvent()
        eventBack()
        setEventNavBar()
    }
    private  fun setControl(){
        btnExit = findViewById(R.id.btnExit)
        btnAddCart = findViewById(R.id.btnAddCart)
        btnBuyNow = findViewById(R.id.btnBuyNow)
        btnComment = findViewById(R.id.btnComment)

        tvNameDetail = findViewById(R.id.tvNameDetail)
        tvPriceDetail = findViewById(R.id.tvPriceDetail)
        tvIdDetait = findViewById(R.id.tvIdDetail)
        tvType = findViewById(R.id.tvType)
        tvDesc = findViewById(R.id.tvDesc)
        tvRating = findViewById(R.id.tvRating)
        tvTotalCMT = findViewById(R.id.tvTotalCmt)

        ivBackDetail = findViewById(R.id.ivBackDetail)
        ivProDetail = findViewById(R.id.ivProDetail)
        ivDefault = findViewById(R.id.ivDefault)

        ratingBar = findViewById(R.id.ratingBar)
        lnBoxCMT = findViewById(R.id.lvBoxCMT)

        //nav
        navbarBott = findViewById<BottomNavigationView>(R.id.bottom_navigationCart)

    }
    private  fun setEvent(){

        //lay id duoc gui
        val getProductID = intent.getStringExtra("productClick")
//        Toast.makeText(this, "Clicked on: $getProductID", Toast.LENGTH_SHORT).show()
        val productId = getProductID
        //connet table product
        val databaseReference = FirebaseDatabase.getInstance().getReference("products")
        //connet table comments
        val databaseReferenceCMT = FirebaseDatabase.getInstance().getReference("Comments")
        if (productId != null) {
            //xu ly product detai
            //xu ly voi duy 1 child co id la proID
            databaseReference.child(productId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        //get data
                        val productName = snapshot.child("name").getValue(String::class.java) ?: "No Name"
                        val productDesc = snapshot.child("desc").getValue(String::class.java) ?: "No Desc"
                        val productType = snapshot.child("type").getValue(String::class.java) ?: "No Type"
                        val productPrice = snapshot.child("price").getValue(Int::class.java) ?: 0
                        val imageUrl2 = snapshot.child("imageUrl").getValue(String::class.java) ?: "No image"

                        //lay anh tu drawble
                        val imageResId2 = resources.getIdentifier(imageUrl2, "drawable", packageName)

                        if (imageResId2 != 0) {
                            Glide.with(this@ProductDetail)
                                .load(imageResId2)
                                .override(400, 150)
                                .into(ivProDetail)
                        }
                        else{
                            //lay anh truc tiep tu url
                            Glide.with(this@ProductDetail)
                                .load("$imageUrl2")
                                .override(400, 150)
                                .into(ivProDetail)
                        }
                        //set data sp2
                        tvNameDetail.text = productName
                        tvPriceDetail.text = "Giá: $productPrice VND"
                        tvIdDetait.text = "Mã: $productId"
                        tvType.text = "Giới tính: $productType"
                        tvDesc.text = productDesc
                        btnComment.setOnClickListener()
                        {
                            if(productId != null){
                                val arr = arrayOf("$productId","$productName","$imageUrl2")
                                val intent = Intent(this@ProductDetail, Comment::class.java)
                                intent.putExtra("productCMT", arr)
                                startActivity(intent)
                            }
                        }
                    } else {
                        Log.e("Firebase", "Product not found")
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Failed to read data", error.toException())
                }
            })
            //ket thuc xu ly prodetail

            //xu ly comment
            databaseReferenceCMT.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var haveCmt = false
                    var cmtnum = 0
                    // Lặp qua tất cả các bình luận
                    for (snapCMT in snapshot.children) {
                        //get id san pham dc danh gia
                        val idProCmt = snapCMT.child("idProduct").getValue(String::class.java)?: "No id"
                        // show if id dc nhan == idProcmt
                        if (idProCmt == productId) {
                            cmtnum ++ // dem so luong cmt

                            val nameUsercmt = snapCMT.child("nameUser").getValue(String::class.java) ?: "No Name"
                            val cmt = snapCMT.child("cmt").getValue(String::class.java)?: "No Comment"
                            // tao view tu layout item
                            val inflater = LayoutInflater.from(this@ProductDetail)
                            val view = inflater.inflate(R.layout.layout_post_comment, lnBoxCMT, false)

                            view.findViewById<TextView>(R.id.tvNameUserCMT).text = nameUsercmt
                            view.findViewById<TextView>(R.id.tvCmt).text = cmt

                            if (cmt != null){
                                haveCmt = true
                            }
                           lnBoxCMT.addView(view)

                        }
                        tvTotalCMT.text = "Tổng cộng $cmtnum đánh giá từ khách hàng"

                        if(haveCmt == true){ //neu sp co cmt -> show cmt
                            lnBoxCMT.visibility = View.VISIBLE
                            ivDefault.visibility = View.GONE
                        }
                        else{ // show image default
                            lnBoxCMT.visibility = View.GONE
                            ivDefault.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Failed to read data", error.toException())
                }
            })
        }


        btnExit.setOnClickListener(){
            finish()
        }

        btnAddCart.setOnClickListener(){
            if(productId != null){
                val intent = Intent(this@ProductDetail, CartActivity::class.java)
                intent.putExtra("productId", productId)
                startActivity(intent)
            }
        }
        btnBuyNow.setOnClickListener(){
            if(productId != null){
                val intent = Intent(this@ProductDetail, CartActivity::class.java)
                intent.putExtra("productId", productId)
                startActivity(intent)
            }
        }

        //ratingbar
        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            tvRating.text = rating.toString()
            when(ratingBar.rating.toInt()){
                1 -> tvRating.text = "1"
                2 -> tvRating.text = "2"
                3 -> tvRating.text = "3"
                4 -> tvRating.text = "4"
                5 -> tvRating.text = "5"
                else ->  tvRating.text = "0"
            }
        }
    }
    // btn back
    private fun eventBack(){
        ivBackDetail.setOnClickListener{
            // Quay lại hoặc hiện thông báo
//            Toast.makeText(this, "Back button clicked", Toast.LENGTH_SHORT).show()
            finish() // Kết thúc activity để quay lại màn hình trước
        }
    }
    //nav
    private fun setEventNavBar() {
        navbarBott.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_search -> {
                    // Xử lý khi chọn Search
                    true
                }

                R.id.nav_shoppingcart -> {
                    // Chuyển
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)

                    true
                }

                R.id.nav_wishlist -> {
                    // Xử lý khi chọn Favorites
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