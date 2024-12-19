package com.example.nhom3_project

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PayMethodPaymentAdapter (
    context: Context,
    private val payMeThodPaymentList: MutableList<PayMethodPayment>
) : ArrayAdapter<PayMethodPayment>(context, 0, payMeThodPaymentList) {
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

        val paymethod = payMeThodPaymentList[position]
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

                holder.content.setText(paymethod.payment)
            }
            clicked = !clicked
        }
        holder.btnsave.setOnClickListener{
            var dataAddressUpdate = holder.content.text.toString()?:""
            if (dataAddressUpdate.isNotEmpty()){
                updatePaymethodPaymentItem(paymethod.id,dataAddressUpdate)
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

        fun bind(paymethodPayment: PayMethodPayment, position: Int) {
            content.setText(paymethodPayment.payment.toString())

            btndel.setOnClickListener {
                if (position >= 0 && position < payMeThodPaymentList.size) {
                    val selectedItem = payMeThodPaymentList[position]
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
        dbRef.child("PayMethodPayment").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                payMeThodPaymentList.clear()
                for (paymentPayMethodSnapshot in snapshot.children) {
                    val id = paymentPayMethodSnapshot.child("id").getValue(String::class.java) ?: ""
                    if (id.isNotEmpty()) {
                        val userid =
                            paymentPayMethodSnapshot.child("userid").getValue(String::class.java)
                                ?: ""
                        val payment = paymentPayMethodSnapshot.child("payment")
                            .getValue(String::class.java) ?: ""
                        val paythethod = PayMethodPayment(id, userid, payment)
                        payMeThodPaymentList.add(paythethod)
                    }
                }
                Log.d("PayMethodAdapter", "Danh sách paymethodsaddresslist: $payMeThodPaymentList")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PayMethodAdapter", "Lỗi tải dữ liệu giỏ hàng: ${error.message}")
            }
        })
    }

    private fun updatePaymethodPaymentItem(
        paymethodpaymentID: String,
        newcontent: String
    ) {
        dbRef.child("PayMethodPayment").child(paymethodpaymentID.toString())
            .child("payment").setValue(newcontent)
            .addOnSuccessListener {
                Log.d(
                    "PayMethodAdapter",
                    "Cập nhật thành công cho phương thức thanh toán  id la: $paymethodpaymentID"
                )
                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Log.e("PayMethodAdapter", "Lỗi cập nhật: ${error.message}")
                Toast.makeText(context, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun removeAddresItem(paymethodaddressid: String, position: Int) {
        if (position >= 0 && position < payMeThodPaymentList.size){
            dbRef.child("PayMethodPayment").child(paymethodaddressid.toString()).removeValue()
                .addOnSuccessListener {
                    payMeThodPaymentList.removeAt(position)
                    notifyDataSetChanged()
                    Toast.makeText(context, "Xóa phương thức thành công!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    Log.e("PayMethodAdapter", "Lỗi xóa phương thức : ${error.message}")
                    Toast.makeText(context, "Lỗi xóa phương thức: ${error.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

}