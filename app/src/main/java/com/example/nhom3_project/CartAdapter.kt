package com.example.nhom3_project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CartAdapter(context: Context, private val productsList: MutableList<Products>, private val updateTotalPrice: () -> Unit) :
    ArrayAdapter<Products>(context, 0, productsList) {
    private lateinit var dbRef: DatabaseReference
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val product = productsList[position]
        holder.bind(product)

        return view
    }

    private inner class ViewHolder(view: View) {
        val productName: TextView = view.findViewById(R.id.tvTenSP)
        val productPrice: TextView = view.findViewById(R.id.tvGiaSP)
        val productImage: ImageView = view.findViewById(R.id.ivProductCart)
        val btnMinus: Button = view.findViewById(R.id.btnMinusCart)
        val btnPlus: Button = view.findViewById(R.id.btnPlusCart)
        val tvSolg: TextView = view.findViewById(R.id.tvSoLg)
        val cbSp: CheckBox = view.findViewById(R.id.cbSelectSPCart)


        fun bind(product: Products) {
            productName.text = product.name
            productPrice.text = product.price.toString()
            val imageResId = context.resources.getIdentifier(product.img, "drawable", context.packageName)
            productImage.setImageResource(if (imageResId != 0) imageResId else R.drawable.baseline_hide_image_24)

            // Đặt sự kiện cho CheckBox
            cbSp.isChecked = product.isSelected
            cbSp.setOnCheckedChangeListener { _, isChecked ->
                product.isSelected = isChecked
                updateTotalPrice()
            }
            // Đặt số lượng
            tvSolg.text = product.quantity.toString()
            btnMinus.setOnClickListener {
                if (product.quantity > 1) {
                    product.quantity -= 1
                    tvSolg.text = product.quantity.toString()
                    updateTotalPrice()
                } else {
                    Toast.makeText(context, "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show()
                }
            }
            btnPlus.setOnClickListener {
                product.quantity += 1
                tvSolg.text = product.quantity.toString()
                updateTotalPrice()
            }
        }
    }


}
