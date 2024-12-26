package com.example.nhom3_project

import Products
import Wishlist
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//
class SearachAdapter(
    private val context: Context,
    private val productsList: MutableList<Products>,
    private var WishlList: MutableList<Wishlist> = mutableListOf()
) : RecyclerView.Adapter<SearachAdapter.ProductViewHolder>() {

    private lateinit var dbRef: DatabaseReference

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = firebaseAuth.currentUser
    private val uid = currentUser?.uid.toString()

    init {
        getDataWislist()
    }

    // ViewHolder class
    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.tvNamePr)
        val productPrice: TextView = view.findViewById(R.id.tvPrice)
        val productImage: ImageView = view.findViewById(R.id.imageView)
        val btnAddtoCart: AppCompatImageButton = view.findViewById(R.id.ibCart)
        val btnLike: AppCompatImageButton = view.findViewById(R.id.ibLike)
        var onItemClick: ((Products) -> Unit)? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false)
        val viewHolder = ProductViewHolder(view)

        view.setOnClickListener {
            viewHolder.onItemClick?.invoke(productsList[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productsList[position]

        holder.productName.text = product.name
        holder.productPrice.text = "${product.price} VND"

        val imageResId = context.resources.getIdentifier(product.imageUrl, "drawable", context.packageName)
        holder.productImage.setImageResource(if (imageResId != 0) imageResId else R.drawable.baseline_hide_image_24)
        // Thiết lập sự kiện click khi người dùng nhấn vào sản phẩm
        holder.onItemClick = {
            // Bạn có thể xử lý hành động khi người dùng click vào sản phẩm tại đây
            val intent = Intent(context, ProductDetail::class.java)
            Log.d("send id" , product.id)
            intent.putExtra("productClick", product.id)  // Truyền ID sản phẩm qua intent
            context.startActivity(intent)
        }

        holder.btnAddtoCart.setOnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra("productClick", product.id)
            context.startActivity(intent)
        }
        val wishlistItem1 = WishlList.find { it.productid == product.id }
        holder.btnLike.setOnClickListener{

            addToWishlist(product.id)
            if (wishlistItem1 != null) {
                if (wishlistItem1.selected == true) {
                    holder.btnLike.setImageResource(R.drawable.ic_wishlist_unselected)
                    wishlistItem1.selected = false
                } else if(wishlistItem1.selected == false){
                    holder.btnLike.setImageResource(R.drawable.ic_wishlist_selected)
                    wishlistItem1.selected = true
                }
            }
        }

    }

    override fun getItemCount(): Int = productsList.size

    private fun getDataWislist() {
        dbRef = FirebaseDatabase.getInstance().reference

        dbRef.child("Wishlist").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                WishlList.clear()
                for (cartSnapshot in snapshot.children) {
                    val userid = cartSnapshot.child("userid").getValue(String::class.java) ?: ""
                    if (userid == uid){
                        val id = cartSnapshot.child("id").getValue(String::class.java) ?: ""
                        val productid = cartSnapshot.child("productid").getValue(String::class.java) ?: "0"
                        val wishlist = Wishlist(id, userid, productid)
                        WishlList.add(wishlist)
                    }
                }
                notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("WishlistAdapter", "Lỗi tải dữ liệu yêu thích: ${error.message}")
            }
        })
    }
    private fun updateWishlist(WishlistId: String) {
        dbRef.child("Wishlist").child(WishlistId.toString()).child("selected").setValue(false)
            .addOnSuccessListener {
                Log.d("WishlistAdapter", "Cập nhật Wishlist thành công: $WishlistId")
            }
            .addOnFailureListener { error ->
                Log.e("WishlistAdapter", "Lỗi cập nhật: ${error.message}")
            }
    }
    fun updateProducts(newList: List<Products>) {
        productsList.clear()
        productsList.addAll(newList)
        notifyDataSetChanged()
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

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
