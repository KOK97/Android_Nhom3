package com.example.nhom3_project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var tvNextspm: TextView
    private lateinit var tvPrespm: TextView
    private lateinit var tvNextspbc: TextView
    private lateinit var tvPrespbc: TextView
    private lateinit var viewFlipperspbc: ViewFlipper
    private lateinit var viewFlipperspm: ViewFlipper

    private lateinit var navbarBott: BottomNavigationView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_home)

        //banner
        val imageSlider = findViewById<ImageSlider>(R.id.bannerSlide)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel("https://trangsuc.vntheme.com/wp-content/uploads/2023/05/banner_Lac.jpg","Trang sức lắc tay"))
        imageList.add(SlideModel("https://image.donghohaitrieu.com/wp-content/uploads/2023/10/trang-suc-bac-nam-nu-chinh-hang.jpg","Trang sức bạc"))
        imageList.add(SlideModel("https://theme.hstatic.net/200000061680/1000549213/14/2banner_top_img.png?v=1351","Nhẫn đá Cubic"))
        imageSlider.setImageList(imageList, ScaleTypes.FIT)

        setControl()
        setEvent()
        setEventNavBar()
    }
    private fun setControl(){
        tvNextspm = findViewById(R.id.tvNextspm)
        tvPrespm = findViewById(R.id.tvPrespm)
        tvNextspbc = findViewById(R.id.tvNextspbc)
        tvPrespbc = findViewById(R.id.tvPrespbc)
        viewFlipperspm = findViewById(R.id.vfSPM)
        viewFlipperspbc = findViewById(R.id.vfSPBC)
        //nav
        navbarBott = findViewById<BottomNavigationView>(R.id.bottom_navigationCart)
    }
    private fun setEvent(){
        //viewFlip spbc
        val inani = AnimationUtils.loadAnimation(this,R.anim.fade_in)
        val outani = AnimationUtils.loadAnimation(this,R.anim.fade_out)
        viewFlipperspbc.setInAnimation(inani)
        viewFlipperspbc.setOutAnimation(outani)
        viewFlipperspbc.setFlipInterval(5000)
        viewFlipperspbc.startFlipping()

        tvNextspbc.setOnClickListener(){

            viewFlipperspbc.showNext()
            viewFlipperspbc.startFlipping()

        }
        tvPrespbc.setOnClickListener(){


            viewFlipperspbc.showPrevious()
            viewFlipperspbc.startFlipping()


        }
//viewFlip spm

        viewFlipperspm.setInAnimation(inani)
        viewFlipperspm.setOutAnimation(outani)
        viewFlipperspm.setFlipInterval(5000)
        viewFlipperspm.startFlipping()
        tvNextspm.setOnClickListener(){

            viewFlipperspm.showNext()
            viewFlipperspm.startFlipping()

        }
        tvPrespm.setOnClickListener(){


            viewFlipperspm.showPrevious()
            viewFlipperspm.startFlipping()


        }

    }
    private fun setEventNavBar(){
        navbarBott.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
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
                    true
                }
                else -> false
            }
        }
    }
}

