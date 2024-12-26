package com.example.nhom3_project

import Products
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SearachAdapter(private val productList: List<Products>) : RecyclerView.Adapter<SearachAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.name
        holder.productPrice.text = "${product.price} VND"

        // Load image with Glide
        val imageUrl = product.imageUrl
        if (imageUrl.startsWith("http")) {
            Glide.with(holder.productImage.context)
                .load(imageUrl)
                .override(150, 100)
                .into(holder.productImage)
        } else {
            val imageResId = holder.productImage.context.resources.getIdentifier(imageUrl, "drawable", holder.productImage.context.packageName)
            Glide.with(holder.productImage.context)
                .load(imageResId)
                .override(150, 100)
                .into(holder.productImage)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.imageView1)
        val productName: TextView = view.findViewById(R.id.tvNamePr)
        val productPrice: TextView = view.findViewById(R.id.tvPrice)
    }
}