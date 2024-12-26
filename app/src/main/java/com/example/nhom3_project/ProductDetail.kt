package com.example.nhom3_project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
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
import com.google.firebase.auth.FirebaseAuth
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

    private lateinit var ivProDetail: ImageView
    private lateinit var tvTotalCMT: TextView

    private lateinit var ivDefault: ImageView
    private lateinit var ivBackDetail: ImageView

    private lateinit var btnAddCart: Button
    private lateinit var btnBuyNow: Button
    private lateinit var btnComment: Button

    private lateinit var ratingBar: RatingBar
    private lateinit var edtComment: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var rvComments: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var ratingBarInput: RatingBar
    private var getProductID: String? = null

    //nav
    private lateinit var navbarBott: BottomNavigationView

    //cua the lo
    private var datapay: MutableList<PayData> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)
        mAuth = FirebaseAuth.getInstance()
        setControl()
        setEvent()
        eventBack()
        setEventNavBar()
    }

    private fun setControl() {
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
        ratingBarInput = findViewById(R.id.ratingBarInput)
        edtComment = findViewById(R.id.edtComment)
        rvComments = findViewById(R.id.rvComments)

        rvComments.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter()
        rvComments.adapter = commentAdapter

        //nav
        navbarBott = findViewById<BottomNavigationView>(R.id.bottom_navigationCart)

    }

    private fun setEvent() {
        //lay id duoc gui
        getProductID = intent.getStringExtra("productClick")
        val productId = getProductID
        //connet table product
        val databaseReference = FirebaseDatabase.getInstance().getReference("products")
        //connet table comments
        val databaseReferenceCMT = FirebaseDatabase.getInstance().getReference("Comments")
        if (productId != null) {
            //xu ly product detai
            //xu ly voi duy 1 child co id la proID
            databaseReference.child(productId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            //get data
                            val productName =
                                snapshot.child("name").getValue(String::class.java) ?: "No Name"
                            val productDesc =
                                snapshot.child("desc").getValue(String::class.java) ?: "No Desc"
                            val productType =
                                snapshot.child("type").getValue(String::class.java) ?: "No Type"
                            val productPrice =
                                snapshot.child("price").getValue(Int::class.java) ?: 0
                            val imageUrl2 = snapshot.child("imageUrl").getValue(String::class.java)
                                ?: "No image"

                            //lay anh tu drawble
                            val imageResId2 =
                                resources.getIdentifier(imageUrl2, "drawable", packageName)

                            if (imageResId2 != 0) {
                                Glide.with(this@ProductDetail)
                                    .load(imageResId2)
                                    .override(400, 150)
                                    .into(ivProDetail)
                            } else {
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
                        } else {
                            Log.e("Firebase", "Product not found")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseError", "Failed to read data", error.toException())
                    }
                })
        }
        btnAddCart.setOnClickListener() {
            if (productId != null) {
                val intent = Intent(this@ProductDetail, CartActivity::class.java)
                intent.putExtra("productID", productId)
                Toast.makeText(this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show()

                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Thêm vào giỏ hàng thất bại", Toast.LENGTH_SHORT).show()
            }
        }
        btnBuyNow.setOnClickListener(){
            val namepro = tvNameDetail.text.toString()?:""
            val pricepro = tvPriceDetail.text.toString()?:""
            datapay.clear()
            datapay.add(PayData("0", productId.toString(), namepro,1))
            if(productId != null){
                val intent = Intent(this, PayActivity::class.java)
                intent.putExtra("product_list", ArrayList(datapay))
                intent.putExtra("totalcart", pricepro)

            } else {
                Toast.makeText(this, "Thêm vào giỏ hàng thất bại", Toast.LENGTH_SHORT).show()
            }
        }
        btnBuyNow.setOnClickListener() {
            if (productId != null) {
                val intent = Intent(this@ProductDetail, CartActivity::class.java)
                intent.putExtra("productId", productId)

                startActivity(intent)
            }
        }

        btnComment.setOnClickListener {
            if (mAuth.currentUser == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để đánh giá", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                return@setOnClickListener
            }

            val commentText = edtComment.text.toString().trim()
            val rating = ratingBarInput.rating

            if (commentText.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập nội dung đánh giá", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (rating == 0f) {
                Toast.makeText(this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            submitComment(commentText, rating)
        }
        productId?.let { id ->
            loadComments(id)
        }

    }

    // btn back
    private fun eventBack() {
        ivBackDetail.setOnClickListener {
            finish()
        }
    }

    //nav
    private fun setEventNavBar() {
        navbarBott.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
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

                R.id.nav_wishlist -> {
                    val intent = Intent(this, WishListActivity::class.java)
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

    private fun submitComment(content: String, rating: Float) {
        val user = mAuth.currentUser ?: return
        //lay id duoc gui
        getProductID = intent.getStringExtra("productClick")
        val productId = getProductID
        val commentRef = FirebaseDatabase.getInstance().getReference("Comments")
        val userRef = FirebaseDatabase.getInstance().getReference("Users")
        val commentId = commentRef.push().key ?: return

        productId?.let { productID ->
            userRef.child(user.uid).child("name")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userName = snapshot.getValue(String::class.java) ?: "Anonymous"

                        val comment = Comment(
                            id = commentId,
                            userId = user.uid,
                            userName = userName,
                            productId = productID,
                            rating = rating,
                            content = content
                        )

                        commentRef.child(commentId).setValue(comment)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@ProductDetail,
                                    "Đánh giá đã được gửi",
                                    Toast.LENGTH_SHORT
                                ).show()
                                edtComment.text.clear()
                                ratingBarInput.rating = 0f
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this@ProductDetail,
                                    "Không thể gửi đánh giá",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "Lỗi khi lấy tên người dùng", error.toException())
                    }
                })
        }

    }

    private fun loadComments(productId: String) {
        val commentRef = FirebaseDatabase.getInstance().getReference("Comments")
        commentRef.orderByChild("productId").equalTo(productId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val comments = mutableListOf<Comment>()
                    var totalRating = 0f
                    snapshot.children.forEach {
                        val comment = it.getValue(Comment::class.java)
                        comment?.let {
                            comments.add(it)
                            totalRating += it.rating
                        }
                    }

                    if (comments.isEmpty()) {
                        ivDefault.visibility = View.VISIBLE
                        rvComments.visibility = View.GONE
                    } else {
                        ivDefault.visibility = View.GONE
                        rvComments.visibility = View.VISIBLE
                        commentAdapter.submitList(comments)

                        val averageRating = totalRating / comments.size
                        tvRating.text = String.format("%.1f", averageRating)
                        ratingBar.rating = averageRating
                        tvTotalCMT.text = "Tổng cộng ${comments.size} đánh giá từ khách hàng"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Comments", "Error loading comments", error.toException())
                }
            })
    }

//    private fun updateProductRating() {
//        val productId = getProductID ?: return
//        val commentsRef = FirebaseDatabase.getInstance().getReference("Comments")
//        val productRef = FirebaseDatabase.getInstance().getReference("products").child(productId)
//
//        commentsRef.orderByChild("productId").equalTo(productId)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    var totalRating = 0f
//                    var count = 0
//
//                    for (child in snapshot.children) {
//                        val comment = child.getValue(Comment::class.java)
//                        if (comment != null) {
//                            totalRating += comment.rating
//                            count++
//                        }
//                    }
//
//                    if (count > 0) {
//                        val averageRating = totalRating / count
//                        productRef.child("averageRating").setValue(averageRating)
//                            .addOnSuccessListener {
//                                Log.d(
//                                    "ProductRating",
//                                    "Đã cập nhật rating trung bình: $averageRating"
//                                )
//                            }
//                            .addOnFailureListener { error ->
//                                Log.e("ProductRating", "Không thể cập nhật rating", error)
//                            }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e("ProductRating", "Lỗi khi cập nhật rating", error.toException())
//                }
//            })
//    }
}