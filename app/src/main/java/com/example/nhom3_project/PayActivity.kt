package com.example.nhom3_project

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PayActivity : AppCompatActivity() {

    private lateinit var ivBackPay: ImageView
    private lateinit var btnAccepp: Button
    private lateinit var adapter: PayAdapter
    private var Billslist: MutableList<Bills> = mutableListOf()
    private var addressPayMethodlist: MutableList<PayMethodAddress> = mutableListOf()
    private var paymentPayMethodlist: MutableList<PayMethodPayment> = mutableListOf()
    private var detailList: MutableList<PayData> = mutableListOf()
    private lateinit var idAddress:String
    private lateinit var idPayment:String
    private lateinit var spinerAddress :Spinner
    private lateinit var spinerPayment :Spinner
    private  lateinit var lvDetail: ListView
    private lateinit var tvTotal: TextView
    private lateinit var dbRef: DatabaseReference
    private lateinit var uid: String
    private lateinit var total: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pay)
        setControll()
        setDataAddressPayMeThod()
        setDataPaymentPayMeThod()
        setEventGetDataPay()
        setEvenSpinerAddress(spinerAddress)
        setEvenSpinerPayment(spinerPayment)
        setEventPay()
        eventBack()
    }
    private fun setControll(){
        //btnBack
        ivBackPay = findViewById(R.id.ivBackPay)
        //btnPay
        btnAccepp = findViewById(R.id.btnAccepp)
        //spiner
        spinerAddress = findViewById(R.id.spPayDiaChi)
        spinerPayment = findViewById(R.id.spPayPhuongThuc)
        //lv
        lvDetail = findViewById(R.id.lvDetailPay)
        //total
        tvTotal= findViewById(R.id.tvPayAllTotal)
        //uid
        uid = "4IkgM1ZTroMf3yLIVcKhbBGp9Ol2"
    }
    private fun setEventGetDataPay(){
        val data: MutableList<PayData> = intent.getParcelableArrayListExtra("product_list") ?: mutableListOf()
        total = intent.getStringExtra("totalcart")?:""
        if (data != null) {
            for (detail in data){
                detailList.add(PayData(detail.cartid,detail.productid,detail.propductname,detail.quality))
            }
            adapter = PayAdapter(this@PayActivity, detailList)
            if (total!= ""){
                tvTotal.text = "$total VND"
            }
            else{
                Toast.makeText(this, "Lỏ r ní", Toast.LENGTH_SHORT).show()
            }
            lvDetail.adapter = adapter
        } else {
            Toast.makeText(this, "Dữ liệu không tồn tại", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setEvenSpinerPayment(spinerPaym : Spinner){
        idPayment = ""
        spinerPaym.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = paymentPayMethodlist[position]
                //set id pay
                idPayment = selectedItem.payment
                Toast.makeText(this@PayActivity, "Selected: pay id $idPayment", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
    private fun setDataPaymentPayMeThod() {
        paymentPayMethodlist = mutableListOf()

        // Khởi tạo
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
                idAddress = selectedItem.deliverylocation
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
    private fun setEventPay(){
        btnAccepp.setOnClickListener{
              Payment(detailList,uid,idAddress,idPayment,total.toDouble())
        }
    }
    private fun Payment(products: List<PayData>, uid: String, addressid: String, paymentid: String, total: Double,){
            val dbRef = FirebaseDatabase.getInstance().getReference("Bills")
            // Tạo key
            val billid = dbRef.push().key ?: ""
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val currentTime = sdf.format(Date())
               val billsitem = Bills(
                   id = billid,
                   userid =uid,
                   products = products,
                   addressid = addressid,
                   paymentid = paymentid,
                   totalpayment =total,
                   creationdate = currentTime,
               )
            if (billid != ""){
                dbRef.child(billid).setValue(billsitem)
                    .addOnSuccessListener {

                        Toast.makeText(this, "Thanh Toán thành công", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Thanh toán lỗi", Toast.LENGTH_SHORT).show()
                    }
                for (data in products){
                    if (data.cartid != ""){
                        removeCartItem(data.cartid)
                    }
                }
            }
            else{
                Toast.makeText(this, "lỗi không tạo được id", Toast.LENGTH_SHORT).show()
            }
    }
    private fun eventBack(){
        ivBackPay.setOnClickListener{
            // Quay lại hoặc hiện thông báo
            Toast.makeText(this, "Back button clicked", Toast.LENGTH_SHORT).show()
            finish() // Kết thúc activity để quay lại màn hình trước
        }
    }
    private fun removeCartItem(cartId: String) {
        dbRef.child("Carts").child(cartId.toString()).removeValue()
    }
}