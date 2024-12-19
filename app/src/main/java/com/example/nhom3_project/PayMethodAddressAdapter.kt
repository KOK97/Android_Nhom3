package com.example.nhom3_project

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.*

class PayMethodAddressAdapter(
    context: Context,
    private val payMeThodListAddress: MutableList<PayMethodAddress>,
) : ArrayAdapter<PayMethodAddress>(context, 0, payMeThodListAddress) {
    private lateinit var dbRef: DatabaseReference

    init {
        getDataPayMethod()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.paymethod_item, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val paymethod = payMeThodListAddress[position]
        holder.bind(paymethod, position)

        //btn edit
        var clicked = false
        holder.btnsave.visibility = View.INVISIBLE
        holder.btnedit.setOnClickListener {
            if (!clicked) {
                holder.btnedit.setImageResource(R.drawable.baseline_refresh_24)
                holder.btndel.visibility = View.INVISIBLE
                holder.btnsave.visibility = View.VISIBLE
                holder.content.isFocusable = true
                holder.content.isFocusableInTouchMode = true
                holder.content.requestFocus()
            } else {
                holder.btnedit.setImageResource(R.drawable.baseline_edit_24)
                holder.btndel.visibility = View.VISIBLE
                holder.btnsave.visibility = View.INVISIBLE
                holder.content.isFocusable = false
                holder.content.isFocusableInTouchMode = false

                holder.content.setText(paymethod.deliverylocation)
            }
            clicked = !clicked
        }
        holder.btnsave.setOnClickListener{
            var dataAddressUpdate = holder.content.text.toString()?:""
            if (dataAddressUpdate.isNotEmpty()){
                updatePaymethodAddressItem(paymethod.id,dataAddressUpdate)
            }
            else
            {
                Toast.makeText(context, "Không thể cập nhật do địa chỉ trống", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return view
    }

    private inner class ViewHolder(view: View) {
        val content: EditText = view.findViewById(R.id.edtPayMIContent)
        val btnedit: ImageButton = view.findViewById(R.id.ibtnPayMIEdit)
        val btndel: ImageButton = view.findViewById(R.id.ibtnPayMIDel)
        val btnsave: ImageButton = view.findViewById(R.id.ibtnPayMISave)

        fun bind(paymethodAddress: PayMethodAddress, position: Int) {
            content.setText(paymethodAddress.deliverylocation.toString())

            btndel.setOnClickListener {
                if (position >= 0 && position < payMeThodListAddress.size) {
                    val selectedItem = payMeThodListAddress[position]
                    removeAddresItem(selectedItem.id, position)
                    Log.d("PayMethodAdapter", "Đã xóa phần tử với ID: ${selectedItem.id}")
                } else {
                    Log.e("PayMethodAdapter", "Vị trí không hợp lệ: $position")
                    Toast.makeText(context, "Lỗi: Không thể xóa địa chỉ!", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun getDataPayMethod() {
        dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("PayMethodAddress").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                payMeThodListAddress.clear()
                for (addressPayMethodSnapshot in snapshot.children) {
                    val id = addressPayMethodSnapshot.child("id").getValue(String::class.java) ?: ""
                    if (id.isNotEmpty()) {
                        val userid =
                            addressPayMethodSnapshot.child("userid").getValue(String::class.java)
                                ?: ""
                        val deliverylocation = addressPayMethodSnapshot.child("deliverylocation")
                            .getValue(String::class.java) ?: ""
                        val paythethod = PayMethodAddress(id, userid, deliverylocation)
                        payMeThodListAddress.add(paythethod)
                    }
                }
                Log.d("PayMethodAdapter", "Danh sách paymethodsaddresslist: $payMeThodListAddress")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PayMethodAdapter", "Lỗi tải dữ liệu giỏ hàng: ${error.message}")
            }
        })
    }

    private fun updatePaymethodAddressItem(
        paymethodaddressID: String,
        newdeliverylocation: String
    ) {
        dbRef.child("PayMethodAddress").child(paymethodaddressID.toString())
            .child("deliverylocation").setValue(newdeliverylocation)
            .addOnSuccessListener {
                Log.d(
                    "PayMethodAdapter",
                    "Cập nhật Thành công cho Địa chỉ thanh toán  id la: $paymethodaddressID"
                )
                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Log.e("PayMethodAdapter", "Lỗi cập nhật: ${error.message}")
                Toast.makeText(context, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun removeAddresItem(paymethodaddressid: String, position: Int) {
      if (position >= 0 && position < payMeThodListAddress.size){
          dbRef.child("PayMethodAddress").child(paymethodaddressid.toString()).removeValue()
              .addOnSuccessListener {
                  payMeThodListAddress.removeAt(position)
                  notifyDataSetChanged()
                  Toast.makeText(context, "Xóa Địa Chỉ thành công!", Toast.LENGTH_SHORT).show()
              }
              .addOnFailureListener { error ->
                  Log.e("PayMethodAdapter", "Lỗi xóa Địa Chỉ : ${error.message}")
                  Toast.makeText(context, "Lỗi xóa sản phẩm: ${error.message}", Toast.LENGTH_SHORT)
                      .show()
              }
      }
    }
}