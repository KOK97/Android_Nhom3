package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class AdminAction : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var nav_logout: com.google.android.material.card.MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,

                SanPhamFragment()
            ).commit()
            navigationView.setCheckedItem(R.id.nav_qlsanpham)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_qlsanpham -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                SanPhamFragment()
            ).commit()

            R.id.nav_qlkhachhang -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                KhachHangFragment()
            ).commit()

            R.id.nav_qldonhang -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                DonHangFragment()
            ).commit()

            R.id.nav_logout -> nav_logout.setOnClickListener {
                // Hiển thị hộp thoại xác nhận đăng xuất
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Xác nhận")
                builder.setMessage("Bạn có muốn đăng xuất không?")

                // Nút "Đồng ý"
                builder.setPositiveButton("Đồng ý") { _, _ ->
                    // Điều hướng về màn hình SplashActivity
                    val intent = Intent(this, SplashActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() // Kết thúc Activity hiện tại
                }


                // Nút "Hủy"
                builder.setNegativeButton("Hủy") { dialog, _ ->
                    dialog.dismiss() // Đóng hộp thoại
                }

                // Hiển thị hộp thoại
                val alertDialog = builder.create()
                alertDialog.show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}