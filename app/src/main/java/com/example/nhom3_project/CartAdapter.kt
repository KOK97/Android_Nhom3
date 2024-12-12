package com.example.nhom3_project

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(context: Context, private val productsList: MutableList<Products>, private val updateTotalPrice: () -> Unit) :
    ArrayAdapter<Products>(context, 0, productsList) {
    private lateinit var dbRef: DatabaseReference
    private lateinit var cartList: MutableList<Cart>
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
        getDataCart()
        holder.bind(product,position)
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

        fun bind(product: Products,position: Int) {
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
            //
            btnDele.setOnClickListener{
               for (cart in cartList){
                   if (cart.productid == product.id){
                       removeCartItem(cart.id,position)
                       Log.d("CartAdapter", "Cart ID: ${cart.id}, Product ID: ${cart.productid}")
                   }
               }
            }
            // Đặt số lượng
            tvSolg.text = product.quantity.toString()
            btnMinus.setOnClickListener {
                if (product.quantity > 1) {
                    product.quantity -= 1
                    tvSolg.text = product.quantity.toString()
                    for (cart in cartList) {
                        if (cart.productid == product.id) {
                            updateCartItem(cart.id,product.quantity)
                        }
                    }
                    updateTotalPrice()
                } else {
                    Toast.makeText(context, "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show()
                }
            }
            btnPlus.setOnClickListener {
                product.quantity += 1
                tvSolg.text = product.quantity.toString()

                for (cart in cartList) {
                    if (cart.productid == product.id) {
                        updateCartItem(cart.id,product.quantity)
                    }
                }
                updateTotalPrice()
            }
        }
    }
    //
    private fun getDataCart() {
        cartList = mutableListOf()
        dbRef = FirebaseDatabase.getInstance().reference

        dbRef.child("Cart").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartList.clear()
                for (cartSnapshot in snapshot.children) {
                    val id = cartSnapshot.child("id").getValue(Int::class.java) ?: 0
                    val userid = cartSnapshot.child("userid").getValue(Int::class.java) ?: 0
                    val productid = cartSnapshot.child("productid").getValue(Int::class.java) ?: 0
                    val quantity = cartSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                    val cart = Cart(id, userid, productid, quantity)
                    cartList.add(cart)
                }
            }
            override fun onCancelled(error: DatabaseError) {
               print(error)
            }
        })
    }
    //
    private fun updateCartItem(cartId: Int, newQuantity: Int) {
        // Đường dẫn đến mục cần cập nhật
        val cartItemRef = dbRef.child("Cart").child(cartId.toString())

        // Dữ liệu cần cập nhật
        val updates = mapOf(
            "quantity" to newQuantity // Cập nhật trường quantity
        )

        // Thực hiện cập nhật
        cartItemRef.updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(context, "Cập nhật số lượng thành công!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, "Lỗi cập nhật: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Hàm xóa sản phẩm khỏi giỏ hàng
    private fun removeCartItem(cartId: Int, position: Int) {
        dbRef.child("Cart").child(cartId.toString()).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show()
                productsList.removeAt(position) // Xóa sản phẩm khỏi danh sách
                notifyDataSetChanged() // Làm mới giao diện
                updateTotalPrice() // Cập nhật tổng tiền
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, "Lỗi xóa sản phẩm: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
