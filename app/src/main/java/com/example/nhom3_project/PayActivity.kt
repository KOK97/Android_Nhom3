package com.example.nhom3_project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PayActivity : AppCompatActivity() {

    private lateinit var ivBackPay: ImageView
    private lateinit var btnAccepp: Button
    private var Billslist: MutableList<Bills> = mutableListOf()
    private var addressPayMethodlist: MutableList<PayMethodAddress> = mutableListOf()
    private var paymentPayMethodlist: MutableList<PayMethodPayment> = mutableListOf()
    private lateinit var idAddress:String
    private lateinit var idPayment:String
    private lateinit var spinerAddress :Spinner
    private lateinit var spinerPayment :Spinner
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pay)
        setControll()
        setDataAddressPayMeThod()
        setDataPaymentPayMeThod()
        setEvenSpinerAddress(spinerAddress)
        setEvenSpinerPayment(spinerPayment)
        eventBack()
        evenPay()

    }
    private fun setControll(){
        //btnBack
        ivBackPay = findViewById(R.id.ivBackPay)
        //btnPay
        btnAccepp = findViewById(R.id.btnAccepp)
        //spiner
        spinerAddress = findViewById(R.id.spPayDiaChi)
        spinerPayment = findViewById(R.id.spPayPhuongThuc)
        //
    }
    private fun setEvenSpinerPayment(spinerPaym : Spinner){
        idPayment = ""
        spinerPaym.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = addressPayMethodlist[position]
                //set id address
                idPayment = selectedItem.toString()
                Toast.makeText(this@PayActivity, "Selected: pay id $idPayment", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
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
                val payment = paymentPayMethodlist.map { it.payment }
                val adapter = ArrayAdapter(
                    this@PayActivity,
                    android.R.layout.simple_spinner_item,
                    payment)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinerPayment.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@PayActivity,
                    "Lỗi khi lấy và gán dữ liệu thanh toán: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }
        )

    }
    private fun setEvenSpinerAddress(spinerAdd : Spinner){
        idAddress = ""
        spinerAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = addressPayMethodlist[position]
                //set id address
                idAddress = selectedItem.toString()
                Toast.makeText(this@PayActivity, "Selected: address id $idAddress", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
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
                val deliveryLocations = addressPayMethodlist.map { it.deliverylocation }
                val adapter = ArrayAdapter(
                    this@PayActivity,
                    android.R.layout.simple_spinner_item,
                    deliveryLocations)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinerAddress.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@PayActivity,
                    "Lỗi khi lấy và gán dữ liệu Địa chỉ : ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }
        )

    }
    private fun evenPay(){
        btnAccepp.setOnClickListener{
            showPaymentSuccess()
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }
    private fun eventBack(){
        ivBackPay.setOnClickListener{
            // Quay lại hoặc hiện thông báo
            Toast.makeText(this, "Back button clicked", Toast.LENGTH_SHORT).show()
            finish() // Kết thúc activity để quay lại màn hình trước
        }
    }
    private fun showPaymentSuccess() {
        Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
    }

}