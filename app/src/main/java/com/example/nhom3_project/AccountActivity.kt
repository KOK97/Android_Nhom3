package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccountActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var tvHTQL: TextView
    private lateinit var btnLogout: com.google.android.material.card.MaterialCardView
    private lateinit var navbarBott: BottomNavigationView
    private lateinit var phanquyen: LinearLayout
    private lateinit var accountDetail: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)
        mAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        setControl()
        setEvent()
        setEventNavBar()
        navbarBott.menu.findItem(R.id.nav_account).isChecked = true
    }

    private fun setControl() {
        btnLogout = findViewById(R.id.btnLogout)
        phanquyen = findViewById(R.id.phanquyen)
        accountDetail = findViewById(R.id.accountDetail)
        //nav
        navbarBott = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        tvHTQL = findViewById(R.id.hethongquanly)

    }

    private fun setEvent() {
        checkUserRole()
        accountDetail.setOnClickListener {
            val intent = Intent(this, AccountDetailActivity::class.java)
            startActivity(intent)
        }
        tvHTQL.setOnClickListener {
            val intent = Intent(this, AdminAction::class.java)
            startActivity(intent)
        }
        btnLogout.setOnClickListener {
            // Hiển thị hộp thoại xác nhận đăng xuất
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Xác nhận")
            builder.setMessage("Bạn có muốn đăng xuất không?")

            // Nút "Đồng ý"
            builder.setPositiveButton("Đồng ý") { _, _ ->
                mAuth.signOut()
                val intent = Intent(this, SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
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

                R.id.nav_search -> {
                    // Xử lý khi chọn Search
                    true
                }
                R.id.nav_wishlist -> {
                    val intent = Intent(this, WishListActivity::class.java)
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
                    // Xử lý khi chọn Favorites
                    true
                }

                else -> false
            }
        }
    }

    private fun checkUserRole() {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            // Lấy dữ liệu người dùng từ Firebase
            databaseReference.child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val role = snapshot.child("role").value.toString()
                            if (role == "Admin") {
                                phanquyen.visibility = View.VISIBLE
                            } else {
                                phanquyen.visibility = View.GONE
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@AccountActivity,
                            "Lỗi khi tải dữ liệu!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}