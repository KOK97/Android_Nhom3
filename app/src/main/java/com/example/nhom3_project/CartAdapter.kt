package com.example.nhom3_project

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.*

class CartAdapter(
    context: Context,
    private val productsList: MutableList<Products>,
    private val updateTotalPrice: () -> Unit
) : ArrayAdapter<Products>(context, 0, productsList) {
    private lateinit var dbRef: DatabaseReference
    private var cartList: MutableList<Cart> = mutableListOf()

    init {
        getDataCart()
    }

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
        holder.bind(product, position)
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
        val btnDele: ImageView = view.findViewById(R.id.ivDeleteCart)

        fun bind(product: Products, position: Int) {
            productName.text = product.name
            productPrice.text = product.price.toString()
            tvSolg.text = product.quantity.toString()

            val imageResId = context.resources.getIdentifier(product.img, "drawable", context.packageName)
            productImage.setImageResource(if (imageResId != 0) imageResId else R.drawable.baseline_hide_image_24)

            cbSp.isChecked = product.isSelected
            cbSp.setOnCheckedChangeListener { _, isChecked ->
                product.isSelected = isChecked
                updateTotalPrice()
            }

            btnDele.setOnClickListener {
                if (cartList.isNotEmpty()) {
                    for (cart in cartList) {
                        if (cart.productid == product.id) {
                            removeCartItem(cart.id, position)
                            Log.d("CartAdapter", "Cart ID: ${cart.id}, Product ID: ${cart.productid}")
                        }
                    }
                }
            }

            btnMinus.setOnClickListener {
                if (product.quantity > 1) {
                    product.quantity -= 1
                    tvSolg.text = product.quantity.toString()
                    updateCartQuantity(product)
                } else {
                    Toast.makeText(context, "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show()
                }
            }

            btnPlus.setOnClickListener {
                product.quantity += 1
                tvSolg.text = product.quantity.toString()
                updateCartQuantity(product)
            }
        }

        private fun updateCartQuantity(product: Products) {
            for (cart in cartList) {
                if (cart.productid == product.id) {
                    updateCartItem(cart.id, product.quantity)
                    break
                }
            }
            updateTotalPrice()
        }
    }

    private fun getDataCart() {
        dbRef = FirebaseDatabase.getInstance().reference

        dbRef.child("Cart").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartList.clear()
                for (cartSnapshot in snapshot.children) {
                    val id = cartSnapshot.child("id").getValue(Int::class.java) ?: 0
                    val userid = cartSnapshot.child("userid").getValue(Int::class.java) ?: 0
                    val productid = cartSnapshot.child("productid").getValue(String::class.java) ?: 0
                    val quantity = cartSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                    val cart = Cart(id, userid, productid.toString(), quantity)
                    cartList.add(cart)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CartAdapter", "Lỗi tải dữ liệu giỏ hàng: ${error.message}")
            }
        })
    }

    private fun updateCartItem(cartId: Int, newQuantity: Int) {
        dbRef.child("Cart").child(cartId.toString()).child("quantity").setValue(newQuantity)
            .addOnSuccessListener {
                Log.d("CartAdapter", "Cập nhật số lượng thành công cho Cart ID: $cartId")
                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Log.e("CartAdapter", "Lỗi cập nhật: ${error.message}")
                Toast.makeText(context, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeCartItem(cartId: Int, position: Int) {
        dbRef.child("Cart").child(cartId.toString()).removeValue()
            .addOnSuccessListener {
                productsList.removeAt(position)
                notifyDataSetChanged()
                updateTotalPrice()
                Toast.makeText(context, "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Log.e("CartAdapter", "Lỗi xóa sản phẩm: ${error.message}")
                Toast.makeText(context, "Lỗi xóa sản phẩm: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
