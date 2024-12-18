package com.example.nhom3_project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class PayMethodActivity : AppCompatActivity() {
    private var addressPayMethodlist: MutableList<PayMethodAddress> = mutableListOf()
    private var paymentPayMethodlist: MutableList<PayMethodPayment> = mutableListOf()
    private lateinit var addressadapter: PayMethodAddressAdapter
    private lateinit var paymentadapter: PayMethodPaymentAdapter
    private lateinit var dbRef: DatabaseReference
    private lateinit var lvAddress: ListView
    private lateinit var lvPayment: ListView
    private lateinit var edtaddress: EditText
    private lateinit var edtPayment: EditText
    private lateinit var btnAddAddress: Button
    private lateinit var btnAddPayment: Button
    private lateinit var btnAddress: Button
    private lateinit var btnPayment: Button
    private lateinit var flPayMDiachi: FrameLayout
    private lateinit var flPayMPayment: FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_method)
        setControll()
        setDataAddressPayMeThod()
        setDataPaymentPayMeThod()
        setEvenViewDinamic()
        setEvenAddAddress()
        setEvenAddPayment()
    }

    private fun setControll() {
        lvAddress = findViewById(R.id.lvPayMAddres)
        lvPayment = findViewById(R.id.lvPayMPay)
        edtaddress = findViewById(R.id.edtPayMDiaChi)
        edtPayment = findViewById(R.id.edtPayMPay)
        btnAddAddress = findViewById(R.id.btnPayMLuuDiaC)
        btnAddPayment = findViewById(R.id.btnPayMLuuPay)
        btnAddress = findViewById(R.id.btnPayMDiaChi)
        btnPayment = findViewById(R.id.btnPayMPayment)
        flPayMDiachi = findViewById(R.id.flPayMAddress)
        flPayMPayment = findViewById(R.id.flPayMPaymernt)
    }
    private fun setDataAddressPayMeThod() {
        addressPayMethodlist = mutableListOf()

        // Khởi tạo Realtime Database Reference
        dbRef = FirebaseDatabase.getInstance().reference

        // Lấy dữ liệu
        dbRef.child("PayMethodAddress").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                addressPayMethodlist.clear()

                for (addressPayMethodSnapshot in snapshot.children) {

                    val id = addressPayMethodSnapshot.child("id").getValue(String::class.java) ?: ""
                    val userid =
                        addressPayMethodSnapshot.child("userid").getValue(String::class.java) ?: ""
                    val deliverylocation = addressPayMethodSnapshot.child("deliverylocation")
                        .getValue(String::class.java) ?: ""
                    val paythethod = PayMethodAddress(id, userid, deliverylocation)

                    addressPayMethodlist.add(paythethod)
                }
                // Gán adapter sau khi đã có dữ liệu
               if (addressPayMethodlist.isNotEmpty()){
                   addressadapter = PayMethodAddressAdapter(this@PayMethodActivity, addressPayMethodlist)
                   lvAddress.adapter = addressadapter
               }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@PayMethodActivity,
                    "Lỗi khi lấy và gán dữ liệu Địa chỉ : ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }
        )

    }
    private fun setEvenViewDinamic(){
        flPayMDiachi.visibility = android.view.View.GONE
        flPayMPayment.visibility = android.view.View.VISIBLE
        btnPayment.setOnClickListener{
                flPayMDiachi.visibility = android.view.View.GONE
                 flPayMPayment.visibility = android.view.View.VISIBLE

        }
        btnAddress.setOnClickListener{
            flPayMDiachi.visibility = android.view.View.VISIBLE
            flPayMPayment.visibility = android.view.View.GONE
        }
    }
    private fun setEvenAddAddress(){
        btnAddAddress.setOnClickListener{
            val dataAddress = edtaddress.text.toString()?:""
            if (dataAddress.isNotEmpty()){
                addAddressMethod(dataAddress)
                edtaddress.text.clear()
            }
            else{
                Toast.makeText(this, "Bạn chưa nhập thông tin địa chỉ muốm thêm ", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun addAddressMethod( deliverylocation: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("PayMethodAddress")

        // Tạo key duy nhất cho mục giỏ hàng
        val addressid = dbRef.push().key ?: return

        // Tạo đối tượng Cart
        val addressItem = PayMethodAddress(
            id = addressid,
            userid = "4IkgM1ZTroMf3yLIVcKhbBGp9Ol2",
            deliverylocation = deliverylocation,

        )

        // Thêm mục giỏ hàng vào Firebase
        dbRef.child(addressid).setValue(addressItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Địa Chỉ đã được thêm!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi thêm Địa Chỉ: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    //payment
    private fun setDataPaymentPayMeThod() {
        paymentPayMethodlist = mutableListOf()

        // Khởi tạo Realtime Database Reference
        dbRef = FirebaseDatabase.getInstance().reference

        // Lấy dữ liệu
        dbRef.child("PayMethodPayment").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                paymentPayMethodlist.clear()

                for (paymentPayMethodSnapshot in snapshot.children) {

                    val id = paymentPayMethodSnapshot.child("id").getValue(String::class.java) ?: ""
                    val userid =
                        paymentPayMethodSnapshot.child("userid").getValue(String::class.java) ?: ""
                    val payment = paymentPayMethodSnapshot.child("payment")
                        .getValue(String::class.java) ?: ""
                    val paythethod = PayMethodPayment(id, userid, payment)

                    paymentPayMethodlist.add(paythethod)
                }
                // Gán adapter sau khi đã có dữ liệu
                if (paymentPayMethodlist.isNotEmpty()){
                    paymentadapter =
                        PayMethodPaymentAdapter(this@PayMethodActivity, paymentPayMethodlist)
                    lvPayment.adapter = paymentadapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@PayMethodActivity,
                    "Lỗi khi lấy và gán dữ liệu thanh toán: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }
        )

    }
    private fun setEvenAddPayment(){
        btnAddPayment.setOnClickListener{
            val dataPayment = edtPayment.text.toString()?:""
            if (dataPayment.isNotEmpty()){
                addPaymentMethod(dataPayment)
                edtPayment.text.clear()
            }
            else{
                Toast.makeText(this, "Bạn chưa nhập thông tin phương thức muốm thêm ", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun addPaymentMethod(payment: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("PayMethodPayment")

        // Tạo key duy nhất cho mục giỏ hàng
        val paymentid = dbRef.push().key ?: return

        // Tạo đối tượng Cart
        val paymentItem = PayMethodPayment(
            id = paymentid,
            userid = "4IkgM1ZTroMf3yLIVcKhbBGp9Ol2",
            payment = payment,
            )

        // Thêm mục giỏ hàng vào Firebase
        dbRef.child(paymentid).setValue(paymentItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Phương thức  đã được thêm!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi thêm phương thức: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}