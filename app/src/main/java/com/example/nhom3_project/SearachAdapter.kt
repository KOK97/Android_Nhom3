package com.example.nhom3_project

import Products
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
) : RecyclerView.Adapter<SearachAdapter.ProductViewHolder>() {

    private lateinit var dbRef: DatabaseReference


    // ViewHolder class
    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.tvNamePr)
        val productPrice: TextView = view.findViewById(R.id.tvPrice)
        val productImage: ImageView = view.findViewById(R.id.imageView)
        val btnAddtoCart: AppCompatImageButton = view.findViewById(R.id.ibCart)
        val ibtnWDelete: AppCompatImageButton = view.findViewById(R.id.ibLike)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productsList[position]

        holder.productName.text = product.name
        holder.productPrice.text = "${product.price} VND"

        val imageResId = context.resources.getIdentifier(product.imageUrl, "drawable", context.packageName)
        holder.productImage.setImageResource(if (imageResId != 0) imageResId else R.drawable.baseline_hide_image_24)


        holder.btnAddtoCart.setOnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra("productClick", product.id)
            context.startActivity(intent)
        }
//        holder.ibtnWDelete.setOnClickListener{
//            if (WishlList.isNotEmpty()){
//                for (wis in WishlList){
//                    if (wis.productid == product.id){
//                        updateWishlist(wis.id)
//                    }
//                }
//            }
//        }
    }

    override fun getItemCount(): Int = productsList.size


    fun updateProducts(newList: List<Products>) {
        productsList.clear()
        productsList.addAll(newList)
        notifyDataSetChanged()
    }
}
