import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nhom3_project.R
import java.text.SimpleDateFormat
import java.util.*

class BillAdapter(private val billsList: MutableList<Bills>) : RecyclerView.Adapter<BillAdapter.BillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = billsList[position]

        holder.textViewId.text = bill.id
        holder.textViewProductName.text = bill.products.joinToString(", ") { it.name }
        holder.textViewTotalPayment.text = "${bill.totalpayment}"

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.textViewCreationDate.text = "${dateFormat.format(bill.creationdate)}"

        holder.textViewUserId.text = "${bill.userid}"
        holder.textViewAddressId.text = "${bill.addressid}"
        holder.textViewPaymentId.text = "${bill.paymentid}"
    }

    override fun getItemCount(): Int {
        return billsList.size
    }

    inner class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewId: TextView = itemView.findViewById(R.id.textViewId)
        val textViewProductName: TextView = itemView.findViewById(R.id.textViewProductName)
        val textViewTotalPayment: TextView = itemView.findViewById(R.id.textViewTotalPayment)
        val textViewCreationDate: TextView = itemView.findViewById(R.id.textViewCreationDate)
        val textViewUserId: TextView = itemView.findViewById(R.id.textViewUserId)
        val textViewAddressId: TextView = itemView.findViewById(R.id.textViewAddressId)
        val textViewPaymentId: TextView = itemView.findViewById(R.id.textViewPaymentId)
    }
    fun updateBills(newList: List<Bills>) {
        billsList.clear()
        billsList.addAll(newList)
        notifyDataSetChanged()
    }
}
