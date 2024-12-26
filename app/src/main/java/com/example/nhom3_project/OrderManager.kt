import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nhom3_project.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderManager : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var billAdapter: BillAdapter
    private lateinit var btn_search: Button
    private lateinit var edt_search_order: EditText
    private var billsList: MutableList<Bills> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.order_manager, container, false)

        recyclerView = view.findViewById(R.id.ordersRecyclerView)
        btn_search = view.findViewById(R.id.btnsearch)
        edt_search_order = view.findViewById(R.id.edt_search_order)
        recyclerView.layoutManager = LinearLayoutManager(context)

        billAdapter = BillAdapter(billsList)
        recyclerView.adapter = billAdapter

        loadData()
        setEvent()
        return view
    }
    private fun setEvent(){
        btn_search.setOnClickListener {
            val searchText = edt_search_order.text.toString().trim().lowercase()
            val filteredProducts = billsList.filter { bill ->
                bill.id.lowercase().contains(searchText) || bill.userid.lowercase()
                    .contains(searchText)
            }
            billAdapter.updateBills(filteredProducts)
        }
    }

    private fun loadData() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Bills")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                billsList.clear()

                for (billSnapshot in dataSnapshot.children) {
                    val billId = billSnapshot.child("id").getValue(String::class.java) ?: ""
                    val cartItemId = billSnapshot.child("cartitemid").getValue(String::class.java) ?: ""
                    val userId = billSnapshot.child("userid").getValue(String::class.java) ?: ""
                    val addressId = billSnapshot.child("addressid").getValue(String::class.java) ?: ""
                    val paymentId = billSnapshot.child("orderstatus").getValue(String::class.java) ?: ""
                    val totalPayment = billSnapshot.child("totalpayment").getValue(Int::class.java) ?: 0

                    val creationDateString = billSnapshot.child("creationdate").getValue(String::class.java)
                    val creationDate = try {
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                        dateFormat.parse(creationDateString ?: "")
                    } catch (e: ParseException) {
                        e.printStackTrace()
                        Date()
                    }

                    val productsSnapshot = billSnapshot.child("products")
                    val productsList = mutableListOf<Products>()
                    for (productSnapshot in productsSnapshot.children) {
                        val productName = productSnapshot.child("propductname").getValue(String::class.java)
                        val product = Products(name = productName ?: "")
                        productsList.add(product)
                    }

                    val bill = Bills(
                        id = billId,
                        cartitemid = cartItemId,
                        userid = userId,
                        addressid = addressId,
                        paymentid = paymentId,
                        totalpayment = totalPayment,
                        creationdate = creationDate,
                        products = productsList
                    )

                    billsList.add(bill)
                }

                billAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("OrderManager", "Failed to load data: ${databaseError.message}")
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

}


