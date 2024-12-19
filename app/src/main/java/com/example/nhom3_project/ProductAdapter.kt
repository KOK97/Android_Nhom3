import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nhom3_project.R
import com.squareup.picasso.Picasso

class ProductAdapter(
    private val context: Context,
    private val productList: MutableList<Products>,
    private val onProductClick: (Products) -> Unit,
    private val onDeleteClick: (Products) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(R.id.tv_id)
        val tvName: TextView = view.findViewById(R.id.tv_product_name)
        val tvCategory: TextView = view.findViewById(R.id.tv_categories)
        val tvType: TextView = view.findViewById(R.id.tv_type)
        val tvPrice: TextView = view.findViewById(R.id.tv_price)
        val tvQuantity: TextView = view.findViewById(R.id.tv_quantity)
        val tvDesc: TextView = view.findViewById(R.id.tv_desc)
        val ivProductImage: ImageView = view.findViewById(R.id.iv_product_image)
        val btnEdit: ImageButton = view.findViewById(R.id.btn_edit)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.tvId.text = product.id
        holder.tvName.text = product.name
        holder.tvCategory.text = product.category
        holder.tvType.text = product.type
        holder.tvPrice.text = "${product.price} VND"
        holder.tvQuantity.text = "${product.quantity}"
        holder.tvDesc.text = product.desc
        Picasso.get().load(product.imageUrl).into(holder.ivProductImage)


        holder.btnEdit.setOnClickListener {

            onProductClick(product)
        }
        holder.btnDelete.setOnClickListener {
            onDeleteClick(product)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
    fun updateProducts(newList: List<Products>) {
        productList.clear()
        productList.addAll(newList)
        notifyDataSetChanged()
    }

}





