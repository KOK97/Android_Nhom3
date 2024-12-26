package com.example.nhom3_project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlin.math.log

class HomeActivity : AppCompatActivity() {
    private lateinit var tvNextspm: ImageView
    private lateinit var tvPrespm: ImageView
    private lateinit var tvNextspbc: ImageView
    private lateinit var tvPrespbc: ImageView

    private lateinit var ibCart: ImageButton
    private lateinit var ibCart1: ImageButton
    private lateinit var ibLike: ImageButton
    private lateinit var ibLike1: ImageButton

    private lateinit var databaseReference: DatabaseReference

    private lateinit var viewFlipperspbc: ViewFlipper
    private lateinit var viewFlipperspm: ViewFlipper

    private lateinit var navbarBott: BottomNavigationView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        setControl()
        setEvent()
        setEventNavBar()
        navbarBott.menu.findItem(R.id.nav_home).isChecked = true
    }

    private fun setControl() {
        tvNextspm = findViewById(R.id.tvNextspm)
        tvPrespm = findViewById(R.id.tvPrespm)
        tvNextspbc = findViewById(R.id.tvNextspbc)
        tvPrespbc = findViewById(R.id.tvPrespbc)

        viewFlipperspm = findViewById(R.id.vfSPM)
        viewFlipperspbc = findViewById(R.id.vfSPBC)

        //nav
        navbarBott = findViewById<BottomNavigationView>(R.id.bottom_navigationCart)
    }

    private fun setEvent() {
        //banner
        val imageSlider = findViewById<ImageSlider>(R.id.bannerSlide)
        val imageList = ArrayList<SlideModel>()
        imageList.add(
            SlideModel(
                "https://trangsuc.vntheme.com/wp-content/uploads/2023/05/banner_Lac.jpg",
                "Trang sức lắc tay"
            )
        )
        imageList.add(
            SlideModel(
                "https://image.donghohaitrieu.com/wp-content/uploads/2023/10/trang-suc-bac-nam-nu-chinh-hang.jpg",
                "Trang sức bạc"
            )
        )
        imageList.add(
            SlideModel(
                "https://theme.hstatic.net/200000061680/1000549213/14/2banner_top_img.png?v=1351",
                "Nhẫn đá Cubic"
            )
        )
        imageSlider.setImageList(imageList, ScaleTypes.FIT)

        //bắt sự kiện nhấn Item vị trí là( đối tượng: giao diện của thư viện denz với vị trí được chọn )
        imageSlider.setItemClickListener(object :
            com.denzcoskun.imageslider.interfaces.ItemClickListener {
            //ghi đè method với item dc chọn theo STT
            override fun onItemSelected(position: Int) {
                val clickedSlide = imageList[position]
                val title = clickedSlide.title
                val imageUrl = clickedSlide.imageUrl
                //chuyen man hinh khi nhan
                val intent = Intent(this@HomeActivity, HomeActivity::class.java)
                startActivity(intent)
                // show
                Toast.makeText(this@HomeActivity, "Clicked on: $title", Toast.LENGTH_SHORT).show()
            }
        })

        // viewflipper spbc
        databaseReference = FirebaseDatabase.getInstance().getReference("products")
        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("MissingInflatedId")
            override fun onDataChange(snapshot: DataSnapshot) {
                var productClick: String
                viewFlipperspbc.removeAllViews()
                val productList = snapshot.children.toList()

                // sort by buycount giam dan
                val sortedProductList = productList.sortedByDescending {
                    it.child("buyCount").getValue(Int::class.java) ?: 0
                }

                for (pair in sortedProductList.chunked(2)) { // tạo vòng lặp với mỗi 2 sản phẩm
                    val inflater = LayoutInflater.from(this@HomeActivity)
                    val view = inflater.inflate(R.layout.home_product_item, viewFlipperspbc, false)

                    if (pair.size == 2) {
                        val firstProduct = pair[0]
                        val secondProduct = pair[1]

                        // get data sp1
                        val product_id1 = firstProduct.child("id").getValue(String::class.java) ?: "0"
                        val productName1 = firstProduct.child("name").getValue(String::class.java) ?: "No name"
                        val productPrice1 = firstProduct.child("price").getValue(Int::class.java) ?: 0
                        val imageUrl1 = firstProduct.child("imageUrl").getValue(String::class.java) ?: "No image"

                        //set data sp1
                        //lay anh tu drawble
                        val imageResId1 = resources.getIdentifier(imageUrl1, "drawable", packageName) //lay id anh tu drawble

                        if (imageResId1 != 0) {
                            //neu co id anh trong drawble thi load len
                            Glide.with(this@HomeActivity)
                                .load(imageResId1)
                                .override(150, 100)
                                .into(view.findViewById<ImageView>(R.id.imageView))
                        }
                        else{
                            //lay anh truc tiep tu url
                            Glide.with(this@HomeActivity)
                                .load("$imageUrl1")
                                .override(150,100)
                                .into(view.findViewById<ImageView>(R.id.imageView))
                        }
                        view.findViewById<TextView>(R.id.tvNamePr).text = productName1
//                        view.findViewById<TextView>(R.id.tvId).text = product_id1
                        view.findViewById<TextView>(R.id.tvPrice).text = "$productPrice1 VND"

                        // get data sp2
                        val product_id2 = secondProduct.child("id").getValue(String::class.java) ?: "0"
                        val productName2 = secondProduct.child("name").getValue(String::class.java) ?: "No name"
                        val productPrice2 = secondProduct.child("price").getValue(Int::class.java) ?: 0
                        val imageUrl2 = secondProduct.child("imageUrl").getValue(String::class.java) ?: "No image"
                        //set data sp2
                        //lay anh tu drawble
                        val imageResId2 = resources.getIdentifier(imageUrl2, "drawable", packageName)

                        if (imageResId2 != 0) {
                            //neu co id anh trong drawble thi load len
                            Glide.with(this@HomeActivity)
                                .load(imageResId2)
                                .override(150, 100)
                                .into(view.findViewById<ImageView>(R.id.imageView1))
                        }
                        else{
                            //lay anh truc tiep tu url
                            Glide.with(this@HomeActivity)
                                .load("$imageUrl2")
                                .override(150,100)
                                .into(view.findViewById<ImageView>(R.id.imageView1))
                        }

//                        view.findViewById<TextView>(R.id.tvId1).text = product_id2
                        view.findViewById<TextView>(R.id.tvNamePr1).text = productName2
                        view.findViewById<TextView>(R.id.tvPrice1).text = "$productPrice2 VND"

                        // set click
                        view.findViewById<ConstraintLayout>(R.id.frspbc).setOnClickListener(){
                            productClick = product_id1
                            val intent = Intent(this@HomeActivity, ProductDetail::class.java)
                            intent.putExtra("productClick", productClick)
                            startActivity(intent)
                        }
                        view.findViewById<ConstraintLayout>(R.id.frspbc1).setOnClickListener(){
                            productClick = product_id2
                            val intent = Intent(this@HomeActivity, ProductDetail::class.java)
                            intent.putExtra("productClick", productClick)
                            startActivity(intent)
                        }
                        val ibCart = view.findViewById<ImageButton>(R.id.ibCart)
                        val ibLike = view.findViewById<ImageButton>(R.id.ibLike)
                        val ibCart1 = view.findViewById<ImageButton>(R.id.ibCart1)
                        val ibLike1 = view.findViewById<ImageButton>(R.id.ibLike1)

                        //chuyển activ khi click
                        ibCart.setOnClickListener(){
                            productClick = product_id1
                            val intent = Intent(this@HomeActivity, CartActivity::class.java)
                            intent.putExtra("productClick", productClick)
                            startActivity(intent)
                        }
                        ibCart1.setOnClickListener(){
                            productClick = product_id2
                            val intent = Intent(this@HomeActivity, CartActivity::class.java)
                            intent.putExtra("productClick", productClick)
                            startActivity(intent)
                        }

                        viewFlipperspbc.addView(view)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", "Failed to read data", databaseError.toException())
            }
        })

        // viewflipper spm
        databaseReference = FirebaseDatabase.getInstance().getReference("products")
        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("MissingInflatedId")
            override fun onDataChange(snapshot: DataSnapshot) {
                var productClick: String
                viewFlipperspm.removeAllViews()
                val productList = snapshot.children.toList()

                for (pair in productList.chunked(2)) { // tạo vòng lặp với mỗi 2 sản phẩm
                    val inflater = LayoutInflater.from(this@HomeActivity)
                    val view = inflater.inflate(R.layout.home_product_item, viewFlipperspm, false)

                    if (pair.size == 2) {
                        val firstProduct = pair[0]
                        val secondProduct = pair[1]

                        // get data sp1
                        val product_id1 = firstProduct.child("id").getValue(String::class.java) ?: "0"
                        val productName1 = firstProduct.child("name").getValue(String::class.java) ?: "No name"
                        val productPrice1 = firstProduct.child("price").getValue(Int::class.java) ?: 0
                        val imageUrl1 = firstProduct.child("imageUrl").getValue(String::class.java) ?: "No image"

                        //lay anh tu drawble
                        val imageResId1 = resources.getIdentifier(imageUrl1, "drawable", packageName)
                        if (imageResId1 != 0) {
                            Glide.with(this@HomeActivity)
                                .load(imageResId1)
                                .override(150, 100)
                                .into(view.findViewById<ImageView>(R.id.imageView))
                        }
                        else{
                            //lay anh truc tiep tu url
                            Glide.with(this@HomeActivity)
                                .load("$imageUrl1")
                                .override(150,100)
                                .into(view.findViewById<ImageView>(R.id.imageView))
                        }
                        view.findViewById<TextView>(R.id.tvNamePr).text = productName1
//                        view.findViewById<TextView>(R.id.tvId).text = product_id1
                        view.findViewById<TextView>(R.id.tvPrice).text = "$productPrice1 VND"

                        // get data sp2
                        val product_id2 = secondProduct.child("id").getValue(String::class.java) ?: "0"
                        val productName2 = secondProduct.child("name").getValue(String::class.java) ?: "No name"
                        val productPrice2 = secondProduct.child("price").getValue(Int::class.java) ?: 0
                        val imageUrl2 = secondProduct.child("imageUrl").getValue(String::class.java) ?: "No image"
                        //set data sp2

                        //lay anh tu drawble
                        val imageResId2 = resources.getIdentifier(imageUrl2, "drawable", packageName)

                        if (imageResId2 != 0) {
                            Glide.with(this@HomeActivity)
                                .load(imageResId2)
                                .override(150, 100)
                                .into(view.findViewById<ImageView>(R.id.imageView1))
                        }
                        else{
                            //lay anh truc tiep tu url
                            Glide.with(this@HomeActivity)
                                .load("$imageUrl2")
                                .override(150,100)
                                .into(view.findViewById<ImageView>(R.id.imageView1))
                        }


//                        view.findViewById<TextView>(R.id.tvId1).text = product_id2
                        view.findViewById<TextView>(R.id.tvNamePr1).text = productName2
                        view.findViewById<TextView>(R.id.tvPrice1).text = "$productPrice2 VND"

                        // set click
                        view.findViewById<ConstraintLayout>(R.id.frspbc).setOnClickListener(){
                            productClick = product_id1
                            val intent = Intent(this@HomeActivity, ProductDetail::class.java)
                            intent.putExtra("productClick", productClick)
                            startActivity(intent)
                        }
                        view.findViewById<ConstraintLayout>(R.id.frspbc1).setOnClickListener(){
                            productClick = product_id2
                            val intent = Intent(this@HomeActivity, ProductDetail::class.java)
                            intent.putExtra("productClick", productClick)
                            startActivity(intent)
                        }
                        val ibCart = view.findViewById<ImageButton>(R.id.ibCart)
                        val ibLike = view.findViewById<ImageButton>(R.id.ibLike)
                        val ibCart1 = view.findViewById<ImageButton>(R.id.ibCart1)
                        val ibLike1 = view.findViewById<ImageButton>(R.id.ibLike1)

                        //chuyển activ khi click
                        ibCart.setOnClickListener(){
                            productClick = product_id1
                            val intent = Intent(this@HomeActivity, CartActivity::class.java)
                            intent.putExtra("productClick", productClick)
                            startActivity(intent)
                        }
                        ibCart1.setOnClickListener(){
                            productClick = product_id2
                            val intent = Intent(this@HomeActivity, CartActivity::class.java)
                            intent.putExtra("productClick", productClick)
                            startActivity(intent)
                        }

                        viewFlipperspm.addView(view)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", "Failed to read data", databaseError.toException())
            }
        })

        //chuyển slide SP bán chạy
        val inani = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val outani = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        viewFlipperspbc.setInAnimation(inani)
        viewFlipperspbc.setOutAnimation(outani)
        viewFlipperspbc.setFlipInterval(10000)
        viewFlipperspbc.startFlipping()

        tvNextspbc.setOnClickListener() {
            viewFlipperspbc.showNext()
            viewFlipperspbc.startFlipping()
        }
        tvPrespbc.setOnClickListener() {
            viewFlipperspbc.showPrevious()
            viewFlipperspbc.startFlipping()
        }
        //chuyển slide sản phẩm mới
        viewFlipperspm.setInAnimation(inani)
        viewFlipperspm.setOutAnimation(outani)
        viewFlipperspm.setFlipInterval(10000)
        viewFlipperspm.startFlipping()
        tvNextspm.setOnClickListener() {
            viewFlipperspm.showNext()
            viewFlipperspm.startFlipping()
        }
        tvPrespm.setOnClickListener() {
            viewFlipperspm.showPrevious()
            viewFlipperspm.startFlipping()
        }


    }

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
